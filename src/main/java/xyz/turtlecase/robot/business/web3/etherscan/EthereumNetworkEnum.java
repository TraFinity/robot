package xyz.turtlecase.robot.business.web3.etherscan;

import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.utils.CommonUtil;

/**
 * ETH网络标识枚举
 */
public enum EthereumNetworkEnum {
    EthereumMainNet("0x1", "Ethereum mainnet", "https://api.etherscan.io/api", Boolean.TRUE),
    Ropsten("0x3", "Ropsten", "https://api-ropsten.etherscan.io/api", Boolean.TRUE),
    Rinkeby("0x4", "Rinkeby", "https://api-rinkeby.etherscan.io/api", Boolean.TRUE),
    Goerli("0x5", "Goerli", "https://api-goerli.etherscan.io/api", Boolean.TRUE);

    private String chainId;
    private String chain;
    private String apiUrl;
    private String apiKey;
    private Boolean enable;

    EthereumNetworkEnum(String chainId, String chain, String apiUrl, Boolean enable) {
        this.chainId = chainId;
        this.chain = chain;
        this.apiUrl = apiUrl;
        this.enable = enable;
        switch (this.chainId) {
            case "0x1":
                this.apiKey = CommonUtil.getEnv("etherscan_api_key");
                break;
            case "0x3":
                this.apiKey = CommonUtil.getEnv("ether_ropsten_api_key");
                break;
            case "0x4":
                this.apiKey = CommonUtil.getEnv("ether_rinkeby_api_key");
                break;
            case "0x5":
                this.apiKey = CommonUtil.getEnv("ether_goerli_api_key");
                break;
            default:
                throw new BaseException("Unsupported Ethereum chainId: " + this.chain);
        }

    }

    public static EthereumNetworkEnum getByChainId(String chainId) {
        if (StringUtils.isBlank(chainId)) {
            return null;
        }

        for(EthereumNetworkEnum ethereumNetworkEnum : EthereumNetworkEnum.values()){
            if (StringUtils.equalsIgnoreCase(chainId, ethereumNetworkEnum.chainId)) {
                return ethereumNetworkEnum;
            }
        }

        return null;
    }

    public String getChainId() {
        return this.chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getChain() {
        return this.chain;
    }

    public void setChain(String chain) {
        this.chain = chain;
    }

    public String getApiUrl() {
        return this.apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getApiKey() {
        return this.apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Boolean getEnable() {
        return this.enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }
}
