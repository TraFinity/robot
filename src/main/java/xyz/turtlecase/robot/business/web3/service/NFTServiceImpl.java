package xyz.turtlecase.robot.business.web3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.nftsync.mapper.NftMapper;
import xyz.turtlecase.robot.business.web3.etherscan.EtherScanApi;
import xyz.turtlecase.robot.business.web3.etherscan.EthereumNetworkEnum;
import xyz.turtlecase.robot.business.web3.holders.HolderAtApi;
import xyz.turtlecase.robot.business.web3.holders.HoldersNetworkEnum;

import java.io.IOException;
import java.util.List;

@Service
public class NFTServiceImpl implements NFTService {
    @Autowired
    private EtherScanApi etherScanApi;
    @Autowired
    private HolderAtApi holderAtApi;
    @Autowired
    private NftMapper nftMapper;

    @Override
    public void syncNFTHolders(String contract) throws IOException {
        Long latestBlockNumber = etherScanApi.getBlockNumberByTime(EthereumNetworkEnum.EthereumMainNet);
        holderAtApi.getHolders(HoldersNetworkEnum.ethereum, contract, latestBlockNumber);
    }

    /**
     * 查询入驻的nft合约地址
     * @return
     */
    public List<String> querySettleNFTContracts() {
        return nftMapper.querySettleNFTContracts();
    }
}
