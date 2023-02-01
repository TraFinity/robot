package xyz.turtlecase.robot.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterBizService;
import xyz.turtlecase.robot.infra.service.RedisLockUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 同步twitter账号到list的MQ消费
 * todo 未启用
 */
@Slf4j
//@Service
public class TwitterListMemberSyncConsumerRedisListener extends AbstractConsumerRedisListener {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String rKeyLock = "lock_twitter_list_member_adding";
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private TwitterBizService twitterBizService;
    @Autowired
    private RedisLockUtil redisLockUtil;

    @Override
    public String getTopic() {
        return "TWITTER-LIST-USER-SYNC";
    }

    @Override
    public void doAction(Message message) {
        TwitterListMemberVO value = (TwitterListMemberVO) redisTemplate.getValueSerializer().deserialize(message.getBody());
        Boolean result = Boolean.FALSE;
        if (value == null || StringUtils.isAnyBlank(value.getListName(), value.getTwitterUserName())) {
            return;
        }

        String requestId = LocalDateTime.now().format(dtf);
        // 获取锁
        Boolean lock = redisLockUtil.tryLock("lock_twitter_list_member_adding", requestId, 5L);
        log.info("twitterListMemberSyncConsumerRedisListener get lock {}", lock);
        if (null != lock && lock) {
            try {
                twitterBizService.addMemberToList(value.getListName(), value.getTwitterUserName());
                result = Boolean.TRUE;
            } catch (Exception e) {
                log.info("re-produce msg, topic:{}, listName:{} twitterUserName:{}", getTopic(), value.getListName(), value.getTwitterUserName());
                redisTemplate.convertAndSend(getTopic(), value);
                log.info("consumer topic:{}, value:{}", getTopic(), value, e);
            } finally {
                log.info("consumer topic:{}, value:{}, result: {}", getTopic(), value, result);
            }
        } else {
            // 休眠5s, 重新将数据放到队列
            // todo 放弃, 容易因为twitter api限流导致死循环
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException var17) {
                throw new RuntimeException(var17);
            } finally {
                redisTemplate.convertAndSend(getTopic(), value);
            }
        }
    }
}
