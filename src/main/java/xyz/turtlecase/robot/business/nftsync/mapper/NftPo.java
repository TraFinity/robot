package xyz.turtlecase.robot.business.nftsync.mapper;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Table;

@Data
@EqualsAndHashCode
@Table(name = "nft")
public class NftPo {
    private Long id;
    /**
     * element平台NFT的唯一标识
     */
    private String slug;
    /**
     * nft名
     */
    private String name;
    /**
     * 描述
     */
    private String description;
    private String featuredImageUrl;
    private String bannerImageUrl;
    private String imageUrl;
    private Integer items;
    /**
     * 合约地址
     */
    private String contract;
    /**
     * eth合约地址
     */
    private String ethAddress;
    /**
     * polygon合约地址
     */
    private String polygonAddress;
    /**
     * bsc合约地址
     */
    private String bscAddress;
    /**
     * ava合约地址
     */
    private String avalancheAddress;
    /**
     * 官网
     */
    private String webSite;
    /**
     * dc地址
     */
    private String discord;
    /**
     * twitter账号
     */
    private String twitter;
    /**
     * 微博账号
     */
    private String weibo;
    /**
     * ins账号
     */
    private String instagram;
    /**
     * fb账号
     */
    private String facebook;
    private String medium;
    private String telegram;
    private String discordUrl;
    private String weiboUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String facebookUrl;
    private String mediumUrl;
    private String telegramUrl;
    /**
     * 是否入驻
     */
    private Integer settled;
}
