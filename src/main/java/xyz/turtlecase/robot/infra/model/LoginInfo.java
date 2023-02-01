package xyz.turtlecase.robot.infra.model;

import javax.persistence.Transient;

import xyz.turtlecase.robot.infra.utils.AssertUtil;


/**
 * 登陆的账户信息
 */
public class LoginInfo {
    /**
     * account pk id
     */
    private Long id;
    /**
     * 会员ID
     */
    private String memberID;
    /**
     * 钱包地址
     */
    private String walletAddress;
    /**
     * twitter号, 仅twitter插件登陆才有记录
     */
    @Transient
    private String twitter;

    /**
     * 账户类型
     *
     * @see AddonsUserTypeEnum
     */
    @Transient
    private Integer addonsUserType;

    public LoginInfo() {
    }

    public LoginInfo(Long id, String memberID, String walletAddress) {

        this.id = id;
        this.memberID = memberID;
        if (null != walletAddress) {
            this.walletAddress = walletAddress.toLowerCase();
        }

    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMemberID() {
        AssertUtil.checkNotNull(this.memberID, "token invalid, can not get memberID");
        return this.memberID;

    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getWalletAddress() {
        AssertUtil.checkNotNull(this.walletAddress, "token invalid, can not get wallet address");

        return this.walletAddress.toLowerCase();
    }

    /**
     * 统一将钱包地址转换为小写
     *
     * @param walletAddress
     */
    public void setWalletAddress(String walletAddress) {

        if (null != walletAddress) {
            this.walletAddress = walletAddress.toLowerCase();
        }

    }

    public String getTwitter() {
        AssertUtil.checkNotNull(this.twitter, "token invalid, can not get twitter");
        return this.twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public Integer getAddonsUserType() {
        AssertUtil.checkNotNull(this.addonsUserType, "token invalid, can not get addonsUserType");
        return this.addonsUserType;
    }

    public void setAddonsUserType(Integer addonsUserType) {
        this.addonsUserType = addonsUserType;
    }

}
