package xyz.turtlecase.robot.business.web3.service;

import java.io.IOException;
import java.util.List;

/**
 * nft接口
 */
public interface NFTService {
    /**
     * 同步nft holder到db
     * @param contract
     * @throws IOException
     */
    void syncNFTHolders(String contract) throws IOException;

    /**
     * 查询入驻的nft合约地址
     * @return
     */
    List<String> querySettleNFTContracts();
}
