package xyz.turtlecase.robot.business.nftsync.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * nft orm
 */
public interface NftMapper extends BaseMapper<NftPo> {
    /**
     * 查询入驻的NFT合约地址
     *
     * @return
     */
    @Select({"<script>",
            " SELECT distinct(contract) AS contract FROM nft where settled=1 ",
            "</script>"})
    List<String> querySettleNFTContracts();
}
