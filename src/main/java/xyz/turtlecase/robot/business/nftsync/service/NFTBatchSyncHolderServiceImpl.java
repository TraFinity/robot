package xyz.turtlecase.robot.business.nftsync.service;

import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.mq.NFTHolderSyncProducerService;

/**
 * 同步NFT holder
 */
@Slf4j
@Service
public class NFTBatchSyncHolderServiceImpl implements NFTBatchSyncHolderService {
    @Autowired
    private AccountService accountService;
    @Autowired
    private NFTHolderSyncProducerService nftHolderSyncProducerService;

    /**
     * 同步全部
     * todo 直接新建异步线程执行, 待改进: 1. 线程池 2. 执行结果异常处理
     */
    public void syncAll() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> walletList = accountService.getWalletList();
                int size = walletList.size();
                log.info("sync nft holder,  account list {}", size);
                int i = 0;

                for (String address : walletList) {
                    try {
                        log.info("publish nft holder {} to message, current {}/{}", address, i, size);
                        nftHolderSyncProducerService.sendMsg(address);
                    } catch (Exception e) {
                        log.error("error put event bus to sync holder address {}", address, e);
                    }
                }
            }
        })).run();
    }
}
