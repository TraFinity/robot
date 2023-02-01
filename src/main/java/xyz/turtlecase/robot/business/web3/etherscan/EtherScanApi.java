package xyz.turtlecase.robot.business.web3.etherscan;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.infra.constant.Constants;
import xyz.turtlecase.robot.infra.utils.DateUtils;
import xyz.turtlecase.robot.infra.utils.http.okhttp.OkHttpClientInstance;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class EtherScanApi {
    private static final Integer MAX_LIMIT = 100;
    private Map<String, String> HTTP_HEADER = new HashMap();

    public EtherScanApi() {
        this.HTTP_HEADER.put("Accept", "application/json");
        this.HTTP_HEADER.put("User-Agent", Constants.HTTP_CLIENT_HEADER_AGENT);
    }

    private OkHttpClient getOkHttpClient() {
        return OkHttpClientInstance.getOkHttpClient();
    }

    private void setHeader(Request.Builder request) {
        Iterator var2 = this.HTTP_HEADER.entrySet().iterator();

        for(Map.Entry<String, String> entry : HTTP_HEADER.entrySet()){
            request.addHeader( entry.getKey(),  entry.getValue());
        }

    }

    public Long getBlockNumberByTime(@NotNull EthereumNetworkEnum networkEnum) throws IOException {
        Long timestampSec = DateUtils.getTimestampInSeconds();
        StringBuilder url = (new StringBuilder(networkEnum.getApiUrl())).append("?");
        url.append("module=block")
                .append("&action=getblocknobytime")
                .append("&timestamp=").append(timestampSec)
                .append("&closest=before")
                .append("&apikey=").append(networkEnum.getApiKey());
        Request request = new Request.Builder()
                .url(url.toString())
                .get()
                .addHeader("Accept", Constants.HTTP_CLIENT_HEADER_CONTENT_TYPE_JSON)
                .addHeader("User-Agent", Constants.HTTP_CLIENT_HEADER_AGENT).build();
        Response response = this.getOkHttpClient().newCall(request).execute();

        assert response.body() != null;

        String res = response.body().string();
        response.close();
        log.info("etherScan getBlockNumberByTime network {} timestamp {}, response {}", networkEnum.getChainId(), timestampSec, response);
        if (!response.isSuccessful()) {
            return null;
        } else if (StringUtils.isBlank(res)) {
            return null;
        } else {
            JSONObject jsonObject = JSON.parseObject(res);
            String status = jsonObject.getString("status");
            String result = jsonObject.getString("result");
            return StringUtils.isNotBlank(status) && StringUtils.isNotBlank(result) && StringUtils.equals("1", status) ? Long.valueOf(result) : null;
        }
    }
}
