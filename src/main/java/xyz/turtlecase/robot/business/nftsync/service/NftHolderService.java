package xyz.turtlecase.robot.business.nftsync.service;

import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.nftsync.mapper.NFTHolderTwitterVO;

/**
 * NFT holder服务接口
 */
@Validated
public interface NftHolderService {
    /**
     * 批量保存合约和持有者关系
     *
     * @param address
     * @param contracts
     */
    void batchSaveWithHolders(String address, List<String> contracts);

    /**
     * 根据合约同步holders
     *
     * @param contract
     * @param holders
     */
    void batchSaveWithContract(@NotBlank String contract, @NotNull List<String> holders);

    /**
     * 获取NFT holders的twitter信息
     *
     * @param address
     * @param contract
     * @return
     */
    List<NFTHolderTwitterVO> getNFTHolderTwitter(String address, String contract);

    /**
     * 获取合约的holder钱包
     *
     * @param contracts
     * @return
     */
    List<String> getAddress(String contracts);
}
