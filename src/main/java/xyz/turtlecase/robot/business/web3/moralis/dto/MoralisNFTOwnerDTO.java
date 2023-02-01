package xyz.turtlecase.robot.business.web3.moralis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class MoralisNFTOwnerDTO {
    private String token_address;
    private String token_id;
    private String owner_of;
    private String token_hash;
    private String amount;
    private String contract_type;
    private String name;
    private String symbol;
    private String token_uri;
    private String metadata;
}
