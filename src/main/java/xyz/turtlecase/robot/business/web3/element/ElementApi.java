package xyz.turtlecase.robot.business.web3.element;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.web3.Chain;
import xyz.turtlecase.robot.business.web3.element.dto.ElementApiResult;
import xyz.turtlecase.robot.business.web3.element.dto.ElementNFTContract;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.utils.CommonUtil;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpClientInstance;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;

import static xyz.turtlecase.robot.infra.constant.Constants.HTTP_CLIENT_HEADER_AGENT;

/**
 * element.market api调用
 */
@Slf4j
@Validated
public class ElementApi {
    private static final Integer MAX_LIMIT = 100;
    private static final String BASE_URL = "https://api.element.market/openapi/v1/";
    /**
     * api key
     */
    private String X_API_KEY;

    public ElementApi() {
        this.X_API_KEY = CommonUtil.getEnv("element_api_token");
        if (StringUtils.isBlank(this.X_API_KEY)) {
            throw new IllegalArgumentException("element market X_API_Key is null");
        }
    }

    public ElementApi(String apiKey) {
        if (StringUtils.isBlank(apiKey)) {
            throw new IllegalArgumentException("element market apiKey is null");
        } else {
            this.X_API_KEY = CommonUtil.getEnv("element_api_token");
        }
    }

    /**
     * 查询nft contract信息
     * @param chain
     * @param contractAddress
     * @return
     * @throws IOException
     */
    public ElementApiResult<ElementNFTContract> getContractInfo(@NotNull Chain chain, @NotBlank String contractAddress) throws IOException {
        StringBuilder url = new StringBuilder("https://api.element.market/openapi/v1/");
        url.append("contract?").append("chain=").append(chain.getChain())
                .append("&contract_address=").append(contractAddress);
        OkHttpClient client = OkHttpClientInstance.getOkHttpClient();
        Request request = new Request.Builder()
                .url(url.toString())
                .get()
                .addHeader("Accept", "application/json")
                .addHeader("User-Agent", HTTP_CLIENT_HEADER_AGENT)
                .addHeader("X-Api-Key", this.X_API_KEY).build();
        Response response = client.newCall(request).execute();

        assert response.body() != null;

        String res = response.body().string();
        if (!response.isSuccessful()) {
            log.info("call element api getContractInfo with chain={} address={}, response={}", chain, contractAddress, res);
            throw new BaseException("An exception occurred in the network request");
        } else {
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (jsonObject.containsKey("status")) {
                throw new BaseException(jsonObject.getString("message"));
            } else {
                ElementApiResult<ElementNFTContract> result = JSON.parseObject(res,
                        new TypeReference<ElementApiResult<ElementNFTContract>>() {
                });
                return result;
            }
        }
    }
}
