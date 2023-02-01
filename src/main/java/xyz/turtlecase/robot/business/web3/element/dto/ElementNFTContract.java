package xyz.turtlecase.robot.business.web3.element.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 合约信息
 */
@Data
@EqualsAndHashCode
public class ElementNFTContract {
    private String createDate;
    private String owner;
    private ElementNFTCollection collection;
}
