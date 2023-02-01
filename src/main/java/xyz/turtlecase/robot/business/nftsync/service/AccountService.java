package xyz.turtlecase.robot.business.nftsync.service;

import java.util.List;

/**
 * 账户查询
 */
public interface AccountService {
    /**
     * 获取有效的钱包
     *
     * @return
     */
    List<String> getWalletList();
}
