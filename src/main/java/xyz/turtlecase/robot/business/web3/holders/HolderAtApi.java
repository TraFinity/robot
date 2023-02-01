package xyz.turtlecase.robot.business.web3.holders;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.constant.Constants;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.utils.AssertUtil;
import xyz.turtlecase.robot.infra.utils.http.okhttp.HttpClientBase;

import java.io.IOException;
import java.util.List;

@Slf4j
public class HolderAtApi extends HttpClientBase {
    private static final String BASE_URL = "https://api.holders.at/";

    public List<String> getHolders(HoldersNetworkEnum network, String contract, Long blockNumber) throws IOException {
        AssertUtil.checkNotNull(network, "network is required");
        AssertUtil.checkNotNull(contract, "contract address is required");
        AssertUtil.checkNotNull(blockNumber, "blockNumber is required");
        StringBuilder url = (new StringBuilder(BASE_URL)).append("holders?");
        url.append("network=").append(network.name())
                .append("&collection=").append(contract)
                .append("&block=").append(blockNumber);
        Request request = new Request.Builder()
                .url(url.toString())
                .get()
                .addHeader("Accept", Constants.HTTP_CLIENT_HEADER_CONTENT_TYPE_JSON)
                .addHeader("User-Agent", Constants.HTTP_CLIENT_HEADER_AGENT)
                .build();
        Response response = this.getOkHttpClient().newCall(request).execute();

        assert response.body() != null;

        String res = response.body().string();
        response.close();
        log.info("HolderAtApi getHolders status {}, params: network {} contract {} blockNumber {}", new Object[]{response.isSuccessful(), network, contract, blockNumber});

        if (response.isSuccessful() && !StringUtils.isBlank(res)) {
            return JSON.parseArray(res, String.class);
        } else {
            log.error("error: HolderAtApi getHolders status {}, params: network {} contract {} blockNumber {}, response: {}", new Object[]{response.isSuccessful(), network, contract, blockNumber, res});
            throw new BaseException("Fail to get holder address");
        }
    }
}
