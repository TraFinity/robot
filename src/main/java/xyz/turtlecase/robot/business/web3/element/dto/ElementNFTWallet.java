package xyz.turtlecase.robot.business.web3.element.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * holder信息
 */
@Data
@EqualsAndHashCode
public class ElementNFTWallet {
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 链信息
     */
    private ElementNFTChain blockChain;
}
