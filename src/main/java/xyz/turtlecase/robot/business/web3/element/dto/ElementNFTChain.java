package xyz.turtlecase.robot.business.web3.element.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ElementNFTChain {
    /**
     * 链名
     */
    private String chain;
    /**
     * 链ID
     */
    private String chainId;
}
