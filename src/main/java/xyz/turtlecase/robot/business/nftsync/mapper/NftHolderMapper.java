package xyz.turtlecase.robot.business.nftsync.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * nft holder ORM
 */
public interface NftHolderMapper extends BaseMapper<NftHolderPo> {
    /**
     * 批量插值钱包持有的合约列表
     *
     * @param address   钱包地址
     * @param contracts 合约
     * @return
     */
    @Insert({"<script>" +
            "insert into nft_holder(address,contract) values " +
            " <foreach collection='contracts' item='contract' separator=',' >" +
            " (#{address},#{contract}) " +
            " </foreach>" +
            "</script>"})
    Integer batchInset(@Param("address") String address, @Param("contracts") List<String> contracts);

    /**
     * 批量插入合约的holders
     *
     * @param contract 合约
     * @param holders  holders钱包列表
     * @return
     */
    @Insert({"<script>" +
            "insert into nft_holder(address,contract) values " +
            " <foreach collection='holders' item='address' separator=',' >" +
            " (#{address},#{contract})" +
            " </foreach>" +
            "</script>"})
    Integer batchInsetWithContract(@Param("contract") String contract, @Param("holders") List<String> holders);

    /**
     * 查询holder关联的twitter账号
     *
     * @param address
     * @param contract
     * @return
     */
    @Select({"<script>",
            " SELECT t.twitter_name, t.wallet_address, n.slug,n.contract FROM nft_holder AS h " +
                    " INNER JOIN nft AS n ON h.contract=n.contract  " +
                    " INNER JOIN twitter_account AS t ON h.address=t.wallet_address " +
                    " WHERE n.settled=1 " +
                    " <when test='address != null'> AND t.wallet_address = #{address} </when> " +
                    " <when test='contract != null'> AND n.contract = #{contract} </when> ",
            "</script>"})
    List<NFTHolderTwitterVO> selectHolderTwitter(@Param("address") String address, @Param("contract") String contract);
}
