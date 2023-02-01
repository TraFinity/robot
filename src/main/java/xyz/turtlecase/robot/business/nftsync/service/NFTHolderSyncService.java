package xyz.turtlecase.robot.business.nftsync.service;

import java.io.IOException;
import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;

/**
 * NFT holder同步服务接口
 */
@Validated
public interface NFTHolderSyncService {
    /**
     * 根据钱包同步合约列表
     *
     * @param address
     * @throws IOException
     */
    void syncByWallet(String address) throws IOException;

    /**
     * 同步合约的holder的twitter账号到指定的twitter list
     *
     * @param holderAddress
     * @param contract
     */
    void syncHolderToTwitterList(String holderAddress, String contract);

    /**
     * 同步合约的holder到db
     *
     * @param contract
     * @throws IOException
     */
    void syncByContract(@NotBlank String contract) throws IOException;
}
