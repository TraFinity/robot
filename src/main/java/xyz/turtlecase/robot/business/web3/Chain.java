package xyz.turtlecase.robot.business.web3;

public enum Chain {
    eth("eth", "0x1"),
    ropsten("ropsten", "0x3"),
    rinkeby("rinkeby", "0x4"),
    goerli("goerli", "0x5"),
    kovan("kovan", "0x2a"),
    polygon("polygon", "0x89"),
    mumbai("mumbai", "0x13881"),
    bsc("bsc", "0x38"),
    bscTestnet("bsc testnet", "0x61"),
    avalanche("avalanche", "0xa86a"),
    avalancheTestNet("avalanche testnet", "0xa869"),
    fantom("fantom", "0xfa"),
    cronos("cronos", "0x19"),
    cronosTestNet("cronos testnet", "0x152");

    private String chain;
    private String hex;

    Chain(String chain, String hex) {
        this.chain = chain;
        this.hex = hex;
    }

    public String getChain() {
        return this.chain;
    }

    public String getHex() {
        return this.hex;
    }
}
