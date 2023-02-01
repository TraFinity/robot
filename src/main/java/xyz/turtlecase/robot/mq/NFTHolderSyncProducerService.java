package xyz.turtlecase.robot.mq;

/**
 * nft holder同步消息
 */
public interface NFTHolderSyncProducerService {
    void sendMsg(String address);
}
