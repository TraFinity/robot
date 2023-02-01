package xyz.turtlecase.robot.business.twitter.biz.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.BaseMapper;

import java.util.List;

public interface TwitterUserMapper extends BaseMapper<TwitterUserPo> {
    /**
     * 查询持有nft的holder的twitter user name
     * @param contract
     * @return
     */
    @Select({"SELECT distinct(t.twitter_name) AS twitter_name from twitter_account AS t " +
            " INNER JOIN nft_holder AS n ON n.address=t.wallet_address " +
            " WHERE n.contract = #{contract}"})
    List<String> selectTwitterWithNFTContract(@Param("contract") String contract);

    /**
     * 查询contract对应的holder的twitter user name及id(id可能为空)
     * @param contract
     * @return
     */
    @Select({"SELECT ta.twitter_name AS user_name, tu.id FROM twitter_account AS ta " +
            " INNER JOIN nft_holder AS n ON ta.wallet_address = n.address " +
            " LEFT JOIN twitter_user AS tu ON ta.twitter_name = tu.user_name " +
            " WHERE n.contract = #{contract}"})
    List<TwitterUserPo> selectUserHoldNFTContract(@Param("contract") String contract);

    /**
     * 新增twitter user信息. 如果ID重复则update
     * @param twitterUserPo
     * @return
     */
    @Insert({"<script> " +
            " INSERT INTO twitter_user(id, user_name) VALUES(#{id}, #{userName}) " +
            " on duplicate key update user_name=#{userName}" +
            "</script>"})
    int insertOnDuplicateUpdate(TwitterUserPo twitterUserPo);
}
