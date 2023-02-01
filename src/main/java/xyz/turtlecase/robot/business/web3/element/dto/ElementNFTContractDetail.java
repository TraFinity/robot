package xyz.turtlecase.robot.business.web3.element.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class ElementNFTContractDetail {
    private ElementNFTChain blockChain;
    private String id;
    private String address;
    private String name;
    private String alias;
    private String category;
    private String slug;
}
