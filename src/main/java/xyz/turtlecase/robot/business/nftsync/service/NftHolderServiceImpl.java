package xyz.turtlecase.robot.business.nftsync.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.nftsync.mapper.NFTHolderTwitterVO;
import xyz.turtlecase.robot.business.nftsync.mapper.NftHolderMapper;
import xyz.turtlecase.robot.business.nftsync.mapper.NftHolderPo;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;

/**
 * NFT holder 服务实现
 */
@Validated
@Service
public class NftHolderServiceImpl implements NftHolderService {
    @Resource
    private NftHolderMapper nftHolderMapper;

    /**
     * 获取NFT holder twitter信息
     *
     * @param address
     * @param contract
     * @return
     */
    @Override
    public List<NFTHolderTwitterVO> getNFTHolderTwitter(String address, String contract) {
        return nftHolderMapper.selectHolderTwitter(address, contract);
    }

    /**
     * 根据合约同步holders
     *
     * @param contract
     * @param holders
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void batchSaveWithContract(@NotBlank String contract, @NotNull List<String> holders) {
        if (!CollectionUtils.isEmpty(holders)) {
            NftHolderPo nftHolderPo = new NftHolderPo();
            nftHolderPo.setContract(contract);
            nftHolderMapper.delete(nftHolderPo);
            nftHolderMapper.batchInsetWithContract(contract, holders);
        }
    }

    /**
     * 批量保存钱包地址持有的合约
     *
     * @param address
     * @param contracts
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void batchSaveWithHolders(String address, List<String> contracts) {
        NftHolderPo nftHolderPo = new NftHolderPo();
        nftHolderPo.setAddress(address);
        nftHolderMapper.delete(nftHolderPo);
        // 不为空时, 插值
        if (!CollectionUtils.isEmpty(contracts)) {
            nftHolderMapper.batchInset(address, contracts);
        }

    }

    @Override
    public List<String> getAddress(String contracts) {
        NftHolderPo nftHolderPo = new NftHolderPo();
        nftHolderPo.setContract(contracts);
        List<NftHolderPo> nftHolderPos = nftHolderMapper.select(nftHolderPo);
        if (nftHolderPos.isEmpty()) {
            return new ArrayList<>();
        }

        return nftHolderPos.stream().map(NftHolderPo::getAddress).collect(Collectors.toList());
    }
}
