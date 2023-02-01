package xyz.turtlecase.robot.business.nftsync.mapper;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;

import lombok.Data;
import lombok.EqualsAndHashCode;
import xyz.turtlecase.robot.infra.model.BaseModel;

/**
 * 账户实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AccountPo extends BaseModel {
    /**
     * 会员号, 自动生成
     */
    @Column(name = "member_id")
    private String memberID;

    /**
     * 钱包地址
     */
    @NotBlank(message = "wallet address is required")
    private String walletAddress;

    /**
     * 账户状态, 0注销 1激活
     */
    private Integer status = 1;

}
