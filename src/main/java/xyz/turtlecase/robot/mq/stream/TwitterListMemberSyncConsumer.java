package xyz.turtlecase.robot.mq.stream;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.twitter.biz.service.TwitterBizService;
import xyz.turtlecase.robot.infra.exception.RateLimitingException;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.JsonUtils;
import xyz.turtlecase.robot.mq.TwitterListMemberVO;

import java.io.IOException;

/**
 * holder twitter添加到list
 */
@Slf4j
@Service("twitterListMemberSyncConsumer")
public class TwitterListMemberSyncConsumer implements StreamListener<String, ObjectRecord<String, String>> {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private TwitterBizService twitterBizService;

    public void onMessage(ObjectRecord<String, String> message) {
        String stream = message.getStream();
        RecordId recordId = message.getId();
        String v = message.getValue();
        Boolean result = Boolean.FALSE;
        log.info("[auto ack] group:[group-a] consumerName[{}] stream:[{}], id:[{}], value:[{}]", "consumer-a", stream, recordId, v);

        try {
            Thread.sleep(1000L);
            TwitterListMemberVO twitterListMemberVO = JsonUtils.jsonToPojo(v, TwitterListMemberVO.class);

            if (twitterListMemberVO == null || StringUtils.isAnyBlank(twitterListMemberVO.getListName(), twitterListMemberVO.getTwitterUserName())) {
                return;
            }

            // 如果已经限速
            boolean rateLimit = redisUtil.hasKey("rate_limit_twitter_list_member_add");

            // 直到拿到锁
            while (rateLimit){
                Thread.sleep(30000L);
                rateLimit = redisUtil.hasKey("rate_limit_twitter_list_member_add");
            }

            twitterBizService.addMemberToList(twitterListMemberVO.getListName(), twitterListMemberVO.getTwitterUserName());
            result = Boolean.TRUE;

        } catch (RateLimitingException e) {
            log.error("redis stream consumer twitter api rate limit error", e);
        } catch (IOException e) {
            log.error("redis stream consumer io error", e);
        } catch (Exception e) {
            log.error("redis stream consumer error", e);
        } finally {
            redisUtil.streamDelete(stream, recordId.getValue());
        }

    }
}
