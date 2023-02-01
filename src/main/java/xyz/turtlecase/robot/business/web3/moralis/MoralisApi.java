package xyz.turtlecase.robot.business.web3.moralis;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import xyz.turtlecase.robot.business.web3.Chain;
import xyz.turtlecase.robot.business.web3.moralis.dto.MoralisApiResult;
import xyz.turtlecase.robot.business.web3.moralis.dto.MoralisNFTCollection;
import xyz.turtlecase.robot.infra.constant.Constants;
import xyz.turtlecase.robot.infra.exception.BaseException;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;
import xyz.turtlecase.robot.infra.utils.CommonUtil;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpClientInstance;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Validated
public class MoralisApi {
    private static final Integer MAX_LIMIT = 100;
    private static final String BASE_URL = "https://deep-index.moralis.io/api/v2/";
    private String X_API_KEY;
    private Map<String, String> HTTP_HEADER = new HashMap();

    public MoralisApi() {
        this.X_API_KEY = CommonUtil.getEnv("moralis_api_token");
        if (StringUtils.isBlank(this.X_API_KEY)) {
            throw new IllegalArgumentException("moralis X_API_Key is required");
        } else {
            this.HTTP_HEADER.put("Accept", Constants.HTTP_CLIENT_HEADER_CONTENT_TYPE_JSON);
            this.HTTP_HEADER.put("User-Agent", Constants.HTTP_CLIENT_HEADER_AGENT);
            this.HTTP_HEADER.put("X-API-Key", this.X_API_KEY);
        }
    }

    public MoralisApi(String apiKey) {
        this.X_API_KEY = apiKey;
        if (StringUtils.isBlank(this.X_API_KEY)) {
            throw new IllegalArgumentException("moralis X_API_Key is required");
        }
    }

    private OkHttpClient getOkHttpClient() {
        return OkHttpClientInstance.getOkHttpClient();
    }

    /**
     * 查询钱包持有的NFT合约记录
     * @param chain
     * @param address
     * @param limit
     * @param cursor
     * @return
     * @throws IOException
     */
    public MoralisApiResult<MoralisNFTCollection> getWalletNFTCollections(@NotNull Chain chain, @NotBlank String address, Integer limit, String cursor) throws IOException {
        if (limit == null) {
            limit = MAX_LIMIT;
        }

        StringBuilder url = new StringBuilder(BASE_URL);
        url.append(address).append("/nft/collections?")
                .append("chain=").append(chain.getChain())
                .append("&limit=").append(limit)
                .append("&format=").append("decimal");

        if (StringUtils.isNotBlank(cursor)) {
            url.append("&cursor=").append(cursor);
        }

        Request request = new Request.Builder()
                .url(url.toString())
                .get()
                .addHeader("Accept", Constants.HTTP_CLIENT_HEADER_CONTENT_TYPE_JSON)
                .addHeader("User-Agent", Constants.HTTP_CLIENT_HEADER_AGENT)
                .addHeader("X-API-Key", this.X_API_KEY)
                .build();
        Response response = getOkHttpClient().newCall(request).execute();

        assert response.body() != null;

        String res = response.body().string();
        response.close();
        if (!response.isSuccessful()) {
            log.info("call moralis api getWalletNFTCollections with chain={} contractAddress={} limit={} cursor={}, response={}", chain, address, limit, cursor, res);
            throw new BaseException("An exception occurred in the network request");
        } else if (StringUtils.isBlank(res)) {
            return null;
        } else {
            MoralisApiResult<MoralisNFTCollection> result = JSON.parseObject(res,
                    new TypeReference<MoralisApiResult<MoralisNFTCollection>>() {
            });
            // 数据有值时, 过滤掉symbol空的值
            if (result != null && !CollectionUtils.isEmpty( result.getResult())) {
                List<MoralisNFTCollection> collectionList = result.getResult().stream().filter(s -> StringUtils.isNotBlank(s.getSymbol())).collect(Collectors.toList());
                result.setResult(collectionList);
            }

            return result;
        }
    }

}
