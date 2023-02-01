package xyz.turtlecase.robot.mq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.nftsync.service.NFTHolderSyncService;

/**
 * 同步holder信息
 */
@Slf4j
@Service("nftHolderSyncConsumerRedisListener")
public class NFTHolderSyncConsumerRedisListener extends AbstractConsumerRedisListener {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private NFTHolderSyncService nftHolderSyncService;
    @Autowired
    private NFTHolderSyncProducerService producerService;

    @Override
    public String getTopic() {
        return "NFT-HOLDER-COLLECTION-SYNC";
    }

    public void doAction(Message message) {
        Object value = redisTemplate.getValueSerializer().deserialize(message.getBody());
        String address = value.toString();
        Boolean result = Boolean.FALSE;

        try {
            nftHolderSyncService.syncByWallet(address);
            result = Boolean.TRUE;
        } catch (Exception e) {
            log.info("re-produce msg, topic:{}, value:{}", "NFT-HOLDER-COLLECTION-SYNC", address);
            producerService.sendMsg(address);
            log.info("consumer topic:{}, value:{}", getTopic(), value, e);
        } finally {
            log.info("consumer topic:{}, value:{}, result: {}", new Object[]{getTopic(), value, result});
        }

    }
}
