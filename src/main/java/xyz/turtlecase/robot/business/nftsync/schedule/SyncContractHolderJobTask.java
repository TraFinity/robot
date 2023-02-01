package xyz.turtlecase.robot.business.nftsync.schedule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.turtlecase.robot.business.nftsync.service.NFTHolderSyncService;
import xyz.turtlecase.robot.business.web3.service.NFTService;
import xyz.turtlecase.robot.infra.service.RedisLockUtil;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;
import xyz.turtlecase.robot.infra.utils.Env;

/**
 * 定时器: 同步NFT合约的holder
 */
@Component
public class SyncContractHolderJobTask {
    private static final Logger logger = LoggerFactory.getLogger(SyncContractHolderJobTask.class);
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String rKeyLockPrefix = "schedule_task_sync_contract_holder_";
    @Autowired
    private RedisLockUtil redisLockUtil;
    @Autowired
    private NFTService nftService;
    @Autowired
    private NFTHolderSyncService nftHolderSyncService;

    /**
     * 初始化后60s执行, 每30分钟执行一次
     */
    @Scheduled(
            initialDelay = 60000L,
            fixedRate = 1800000L
    )
    public void jobTask() {
        logger.info("starting schedule task for sync nft contract holderJobTask, env:[{}]", Env.getEnv());
        // 非正式环境, 不执行
        if (!Env.isPrdEnv()) {
            logger.info("finish schedule task for sync nft contract holderJobTask");
            return;
        }

        List<String> contracts = nftService.querySettleNFTContracts();
        if (CollectionUtils.isEmpty( contracts)) {
            logger.info("finish schedule task for sync nft contract holderJobTask, none settle  contract info");
            return;
        }

        String requestId = LocalDateTime.now().format(dtf);
        String rKeyLock = null;

        for (String contract : contracts) {
            try {
                logger.info("begin schedule task for sync nft contract holderJobTask, contract: {} ", contract);
                rKeyLock = rKeyLockPrefix + contract;
                // 获取锁, 并锁10分钟
                Boolean lock = redisLockUtil.tryLock(rKeyLock, requestId, 600L);
                if (null != lock && lock) {
                    nftHolderSyncService.syncByContract(contract);
                }

                logger.info("finish schedule task for sync nft contract holderJobTask, contract: {} ", contract);
            } catch (IOException e) {
                logger.error("error schedule task for sync nft contract holderJobTask, contract: {} ", contract, e);
            } finally {
                // 释放锁
                if (StringUtils.isNotBlank(rKeyLock)) {
                    redisLockUtil.releaseLock(rKeyLock, requestId);
                }
            }
        }
        logger.info("finish schedule task for sync nft contract holderJobTask");
    }
}
