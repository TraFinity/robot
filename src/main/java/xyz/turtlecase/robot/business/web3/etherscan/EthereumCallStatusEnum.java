package xyz.turtlecase.robot.business.web3.etherscan;

/**
 * ethereum api调用结果状态
 */
public enum EthereumCallStatusEnum {
    FAILED(0, "failed"),
    SUCCESS(1, "success"),
    SERVICE_ERROR(-1, "service error");

    private Integer status;
    private String name;

    EthereumCallStatusEnum(Integer status, String name) {
        this.status = status;
        this.name = name;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
