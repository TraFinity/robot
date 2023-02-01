package xyz.turtlecase.robot.business.web3.etherscan.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class EthTransactionResult {
    private String blockHash;
    private String blockNumber;
    private String from;
    private String gas;
    private String gasPrice;
    private String maxFeePerGas;
    private String maxPriorityFeePerGas;
    private String hash;
    private String input;
    private String nonce;
    private String to;
    private String transactionIndex;
    private String value;
    private String type;
    private String chainId;
    private String v;
    private String r;
    private String s;
}
