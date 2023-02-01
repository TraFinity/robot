package xyz.turtlecase.robot.business.web3.moralis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MoralisNFTCollection {
    private String token_address;
    private String contract_type;
    private String name;
    private String symbol;
}
