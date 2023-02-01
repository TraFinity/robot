package xyz.turtlecase.robot.business.web3;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.web3j.crypto.WalletUtils;

@Slf4j
public final class AddressUtils {
    public static Boolean isEthAddress(String address) {
        return StringUtils.isBlank(address) ? false : WalletUtils.isValidAddress(address);
    }
}
