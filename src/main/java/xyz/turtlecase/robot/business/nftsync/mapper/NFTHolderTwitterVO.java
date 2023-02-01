package xyz.turtlecase.robot.business.nftsync.mapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * NFT holder与twitter账号关联VO
 */
@Data
@EqualsAndHashCode
public class NFTHolderTwitterVO {
    /**
     * twitter账号
     */
    private String twitterName;
    /**
     * 钱包地址
     */
    private String walletAddress;
    /**
     * element网站上的NFT唯一标记
     */
    private String slug;
    /**
     * 合约地址
     */
    private String contract;

}
