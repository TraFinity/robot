package xyz.turtlecase.robot.infra.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统启动加载的配置
 */
@Getter
@Component
public class ConfigProperties {
    /**
     * 运行环境, release或dev
     */
    @Value("#{systemProperties['active_env']}")
    private String activeEnv;

    /**
     * 运行环境的基本路径, 存放日志等信息, 绝对路径
     */
    @Value("#{systemProperties['base_path']}")
    private String basePath;

    /**
     * 图片缓存路径, 已废弃, 存放到OBS
     *
     * @deprecated
     */
    @Value("#{systemProperties['pic_path']}")
    private String picPath;

    /**
     * etherscan api key, 用于访问etherscan api
     */
    @Value("#{systemProperties['etherscan_api_key']}")
    private String etherscanApiKey;

    /**
     * 同上, etherscan ropsten api key
     */
    @Value("#{systemProperties['ether_ropsten_api_key']}")
    private String etherRopstenApiKey;

    /**
     * 同上, etherscan rinkeby api key
     */
    @Value("#{systemProperties['ether_rinkeby_api_key']}")
    private String etherRinkebyApikey;

    /**
     * 同上, etherscan goerli api key
     */
    @Value("#{systemProperties['ether_goerli_api_key']}")
    private String etherGoerliApiKey;

    /**
     * infura project url
     */
    @Value("#{systemProperties['web3_ent_point_url']}")
    private String web3EntPointUrl;

    /**
     * TCG big brother合约地址
     */
    @Value("#{systemProperties['big_brother_contract_address']}")
    private String bigBrotherContractAddress;

    /**
     * 对应TCG big brother 合约的链标记, 参考https://eips.ethereum.org/EIPS/eip-155
     */
    @Value("#{systemProperties['big_brother_contract_chain_id']}")
    private String bigBrotherContractChainId;

    /**
     * TCG bigbrother erc721合约地址
     */
    @Value("#{systemProperties['big_brother_contract_erc721_address']}")
    private String bigBrotherContractERC721Address;

    /**
     * 私钥
     */
    @Value("#{systemProperties['big_brother_contract_admin_private_key']}")
    private String BigBrotherContractAdminPrivateKey;

    /**
     * tcg钱包地址
     */
    @Value("#{systemProperties['turtlecase_address']}")
    private String turtleCaseAddress;

    /**
     * 是否启用twitter oAuth token 刷新定时器(TCGTOTHEMOON账号的)
     */
    @Value("#{systemProperties['enable_twitter_refresh_token']}")
    private boolean enableTwitterRefreshToken = false;

    /**
     * 是否发送邮件
     */
    @Value("#{systemProperties['enable_email_send']}")
    private boolean enableEmailSend = false;

    /**
     * 邮件模板
     */
    @Value("#{systemProperties['template_path']}")
    private String templatePath;

    /**
     * open api token
     *
     * @deprecated
     */
    @Value("#{systemProperties['open_api_token']}")
    private String openApiToken;

    /**
     * moralis api token
     */
    @Value("#{systemProperties['moralis_api_token']}")
    private String moralisApiToken;

    /**
     * web app url
     */
    @Value("#{systemProperties['dongdu_base_url']}")
    private String dongduUrl;

    public boolean isPrdEnv() {
        return StringUtils.equalsIgnoreCase("release", activeEnv);
    }

}
