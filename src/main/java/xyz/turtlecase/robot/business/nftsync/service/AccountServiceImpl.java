package xyz.turtlecase.robot.business.nftsync.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.business.nftsync.mapper.AccountMapper;

/**
 * 账户服务
 */
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;

    /**
     * 获取有效的钱包地址
     *
     * @return
     */
    public List<String> getWalletList() {
        return accountMapper.getWalletList();
    }
}
