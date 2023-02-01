package xyz.turtlecase.robot.business.nftsync.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * 账户ORM
 */
public interface AccountMapper extends BaseMapper<AccountPo> {
    /**
     * 查询激活状态的账户钱包列表
     * todo 数量有可能越来越大, 注意分页或增量
     *
     * @return
     */
    @Select({" SELECT wallet_address FROM account  WHERE status=1 "})
    List<String> getWalletList();
}
