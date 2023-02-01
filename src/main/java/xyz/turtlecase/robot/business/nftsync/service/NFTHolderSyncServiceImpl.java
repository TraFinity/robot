package xyz.turtlecase.robot.business.nftsync.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.validation.constraints.NotBlank;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.Record;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.turtlecase.robot.business.nftsync.mapper.NFTHolderTwitterVO;
import xyz.turtlecase.robot.business.web3.AddressUtils;
import xyz.turtlecase.robot.business.web3.Chain;
import xyz.turtlecase.robot.business.web3.etherscan.EtherScanApi;
import xyz.turtlecase.robot.business.web3.etherscan.EthereumNetworkEnum;
import xyz.turtlecase.robot.business.web3.holders.HolderAtApi;
import xyz.turtlecase.robot.business.web3.holders.HoldersNetworkEnum;
import xyz.turtlecase.robot.business.web3.moralis.MoralisApi;
import xyz.turtlecase.robot.business.web3.moralis.dto.MoralisApiResult;
import xyz.turtlecase.robot.business.web3.moralis.dto.MoralisNFTCollection;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.service.RedisUtil;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;
import xyz.turtlecase.robot.infra.utils.JsonUtils;
import xyz.turtlecase.robot.mq.MQMetaEnum;
import xyz.turtlecase.robot.mq.MQProducerService;
import xyz.turtlecase.robot.mq.TwitterListMemberVO;

/**
 * NFT holder同步实现
 */
@Slf4j
@Service
public class NFTHolderSyncServiceImpl implements NFTHolderSyncService {
    /**
     * 每页限制条目
     */
    private static final Integer MAX_LIMIT = 100;
    @Autowired
    private MoralisApi moralisApi;
    @Autowired
    private EtherScanApi etherScanApi;
    @Autowired
    private HolderAtApi holderAtApi;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private NftHolderService nftHolderService;
    @Autowired
    private MQProducerService MQProducerService;

    /**
     * 同步holder的twitter账号到twitter list
     *
     * @param holderAddress
     * @param contract
     */
    @Override
    public void syncHolderToTwitterList(String holderAddress, String contract) {
        List<NFTHolderTwitterVO> nftHolderTwitterVOS = nftHolderService.getNFTHolderTwitter(holderAddress, contract);
        // 有记录时
        if (!CollectionUtils.isEmpty(nftHolderTwitterVOS)) {
            log.info("sync holder to twitter list, size {}", nftHolderTwitterVOS.size());

            for (NFTHolderTwitterVO nftHolderTwitterVO : nftHolderTwitterVOS) {
                TwitterListMemberVO value = new TwitterListMemberVO();
                value.setListName(nftHolderTwitterVO.getSlug());
                value.setTwitterUserName(nftHolderTwitterVO.getTwitterName());

                // 发送到redis stream
                String str = JsonUtils.objectToJson(value);
                Record<String, String> record = StreamRecords.objectBacked(str).withStreamKey(MQMetaEnum.STREAM_TOPIC_TWITTER_LIST_SYNC.getTopic());
                redisUtil.streamAddRecord(record);
            }
        }
    }

    /**
     * 根据钱包账户进行同步持有的合约
     *
     * @param address 钱包地址
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void syncByWallet(String address) throws IOException {
        // 仅同步合法的钱包
        if (AddressUtils.isEthAddress(address)) {

            List<String> contracts = null;
            // moralis查询钱包在eth链上持有的NFT合约
            MoralisApiResult<MoralisNFTCollection> moralisApiResult = moralisApi.getWalletNFTCollections(Chain.eth, address, MAX_LIMIT, null);
            if (moralisApiResult != null && !CollectionUtils.isEmpty(moralisApiResult.getResult())) {
                Set<String> contractSet = new HashSet();

                for (MoralisNFTCollection nftCollection : moralisApiResult.getResult()) {
                    if (nftCollection != null && !StringUtils.isBlank(nftCollection.getToken_address())) {
                        contractSet.add(nftCollection.getToken_address());
                    }
                }

                contracts = new ArrayList(contractSet);
            }

            nftHolderService.batchSaveWithHolders(address, contracts);
            log.info("sync wallet {} collection done", address);
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void syncByContract(@NotBlank String contract) throws IOException {
        String key = "ether_scan_" + EthereumNetworkEnum.EthereumMainNet.name() + "_block_number";
        String bn = redisUtil.getKey(key);
        Long latestBlockNumber = null;
        if (StringUtils.isNotBlank(bn)) {
            latestBlockNumber = Long.valueOf(bn);

        } else {
            // 从etherscan查询最新的区块号
            latestBlockNumber = etherScanApi.getBlockNumberByTime(EthereumNetworkEnum.EthereumMainNet);
            if (latestBlockNumber != null) {
                redisUtil.setKey(key, latestBlockNumber.toString(), 60L, TimeUnit.SECONDS);
            }
        }

        // 如果没有最新区块号
        if (latestBlockNumber == null) {
            throw new BaseException("Network error, can not get eth block number");
        } else {
            // 查询holder列表
            // todo holder.at这个网站不靠谱的, 不稳定, 最好从付费中间件取
            List<String> holders = holderAtApi.getHolders(HoldersNetworkEnum.ethereum, contract, latestBlockNumber);
            if (!CollectionUtils.isEmpty(holders)) {
                nftHolderService.batchSaveWithContract(contract, holders);
            }
        }
    }
}
