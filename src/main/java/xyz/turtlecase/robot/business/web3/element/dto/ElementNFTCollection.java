package xyz.turtlecase.robot.business.web3.element.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class ElementNFTCollection {
    private String id;
    private String name;
    /**
     * 合约唯一标记
     */
    private String slug;
    private String description;
    private Integer royalty;
    private String createDate;
    private String royaltyAddress;
    private Integer platformSellerFee;
    private String imageUrl;
    private String bannerImageUrl;
    private String featuredImageUrl;
    private String externalUrl;
    private String twitterUrl;
    private String instagramUrl;
    private String facebookUrl;
    private String mediumUrl;
    private String telegramUrl;
    private String discordUrl;
    private List<ElementNFTContractDetail> contracts;
    private List<ElementNFTOwner> owners;
}
