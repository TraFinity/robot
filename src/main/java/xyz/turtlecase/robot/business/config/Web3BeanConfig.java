package xyz.turtlecase.robot.business.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.turtlecase.robot.business.web3.element.ElementApi;
import xyz.turtlecase.robot.business.web3.etherscan.EtherScanApi;
import xyz.turtlecase.robot.business.web3.holders.HolderAtApi;
import xyz.turtlecase.robot.business.web3.moralis.MoralisApi;

/**
 * web3 中间件注册
 */
@Configuration
public class Web3BeanConfig {
    @Bean
    public ElementApi createElementApi() {
        return new ElementApi();
    }

    @Bean
    public EtherScanApi createEtherScanApi() {
        return new EtherScanApi();
    }

    @Bean
    public HolderAtApi createHolderAtApi() {
        return new HolderAtApi();
    }

    @Bean
    public MoralisApi createMoralisApi() {
        return new MoralisApi();
    }
}
