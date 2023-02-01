package xyz.turtlecase.robot.business.nftsync.mapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;

/**
 * NFT合约与holder关联PO
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "nft_holder")
public class NftHolderPo {
    /**
     * 合约地址
     */
    private String contract;

    /**
     * holder钱包地址
     */
    private String address;
}
