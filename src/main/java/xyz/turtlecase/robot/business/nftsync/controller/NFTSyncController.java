package xyz.turtlecase.robot.business.nftsync.controller;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import xyz.turtlecase.robot.business.nftsync.service.NFTBatchSyncHolderService;
import xyz.turtlecase.robot.business.nftsync.service.NFTHolderSyncService;
import xyz.turtlecase.robot.infra.constant.Constants;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.web.BaseController;
import xyz.turtlecase.robot.infra.web.RestResult;
import xyz.turtlecase.robot.mq.NFTHolderSyncProducerService;

/**
 * NFT同步
 */
@Slf4j
@RestController
public class NFTSyncController extends BaseController {
    @Autowired
    private NFTHolderSyncService nftHolderSyncService;
    @Autowired
    private NFTBatchSyncHolderService nftBatchSyncHolderService;
    @Autowired
    private NFTHolderSyncProducerService nftHolderSyncProducerService;

    /**
     * 同步钱包的NFT Collection
     *
     * @param address 钱包地址
     * @return
     */
    @PostMapping({"/nft/holder/{address}/syncCollection"})
    @ResponseBody
    public RestResult<String> syncCollection(@PathVariable(name = "address") String address) {
        try {
            nftHolderSyncService.syncByWallet(address);
        } catch (IOException var3) {
            log.error("sync wallet {} collection error", address, var3);
            throw new BaseException(Constants.HTTP_NETWORK_ERROR_MESSAGE);
        }

        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * 批量同步钱包的NFT Collection
     *
     * @return
     */
    @PostMapping({"/nft/holder/batchSyncCollection"})
    @ResponseBody
    public RestResult<String> syncCollection() {
        nftBatchSyncHolderService.syncAll();
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * 异步同步钱包NFT的Collection
     *
     * @param address 钱包地址
     * @return
     */
    @PostMapping({"/nft/holder/{address}/syncCollectionMQPub"})
    @ResponseBody
    public RestResult<String> syncCollectionMQPub(@PathVariable(name = "address") String address) {
        nftHolderSyncProducerService.sendMsg(address);
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * 异步同步twitter list成员
     *
     * @param address
     * @param contract
     * @return
     */
    @PostMapping({"/nft/holder/syncTwitterListMQPub"})
    @ResponseBody
    public RestResult<String> syncTwitterListMQPub(@RequestParam(name = "address", required = false) String address,
                                                   @RequestParam(name = "contract", required = false) String contract) {
        nftHolderSyncService.syncHolderToTwitterList(address, contract);
        return Constants.RESPONSE_SUCCESS;
    }

    /**
     * 同步合约的holder列表
     *
     * @param contract
     * @return
     * @throws IOException
     */
    @PostMapping({"/nft/contract/{contract}/syncHolders"})
    @ResponseBody
    public RestResult<String> syncHolders(@PathVariable(name = "contract") String contract) throws IOException {
        nftHolderSyncService.syncByContract(contract);
        return Constants.RESPONSE_SUCCESS;
    }
}
