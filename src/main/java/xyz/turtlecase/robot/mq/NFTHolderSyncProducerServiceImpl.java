package xyz.turtlecase.robot.mq;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.web3.AddressUtils;

@Slf4j
@Service
public class NFTHolderSyncProducerServiceImpl implements NFTHolderSyncProducerService {
    @Autowired
    private RedisTemplate redisTemplate;

    public void sendMsg(String address) {
        // 钱包地址校验
        if (!StringUtils.isBlank(address)) {
            if (!AddressUtils.isEthAddress(address)) {
                log.info("invalid address {}", address);
            } else {
                redisTemplate.convertAndSend("NFT-HOLDER-COLLECTION-SYNC", address);
            }
        }
    }
}
