package xyz.turtlecase.robot.business.nftsync.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import xyz.turtlecase.robot.business.nftsync.service.NFTHolderSyncService;
import xyz.turtlecase.robot.infra.utils.Env;

/**
 * 定时器: 同步NFT holder的twitter账号到list列表
 */
@Component
public class SyncTwitterListJobTask {
    private static final Logger logger = LoggerFactory.getLogger(SyncTwitterListJobTask.class);
    @Autowired
    private NFTHolderSyncService nftHolderSyncService;

    /**
     * 每晚2点执行
     */
    @Scheduled(
            cron = "0 0 2 * * *"
    )
    public void jobTask() {
        logger.info("starting schedule task for sync nft holder to twitter list JobTask, env:[{}]", Env.getEnv());
        // 非正式环境不执行
        if (!Env.isPrdEnv()) {
            logger.info("finish schedule task for sync nft holder to twitter list JobTask");
            return;
        }
        nftHolderSyncService.syncHolderToTwitterList(null, null);
        logger.info("finish schedule task for sync nft holder to twitter list JobTask");

    }
}
