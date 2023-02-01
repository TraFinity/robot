package xyz.turtlecase.robot.business.twitter.biz.service;

import org.apache.commons.lang3.StringUtils;
import xyz.turtlecase.robot.business.twitter.api.dto.TwitterResponse;
import xyz.turtlecase.robot.infra.utils.CollectionUtils;

import java.util.List;

/**
 * twitter 工具类
 */
public final class TwitterUtils {
    /**
     * 通过twitter user url解析出账号
     * @param userUrl
     * @return
     */
    public static String getUserName(String userUrl) {
        if (StringUtils.isBlank(userUrl)) {
            return null;
        }

        String u = StringUtils.removeStartIgnoreCase(userUrl, "https://twitter.com/");
        if (StringUtils.startsWith(u, "@")) {
            u = StringUtils.removeStart(u, "@");
        }

        return StringUtils.substringBefore(u, "/");
    }

    /**
     * 抽取出tweet id
     * https://twitter.com/nevermind4100/status/1574811871370158080
     * https://twitter.com/angelmonart/status/1576146159961186305?s=20&t=taicX1-VbBez34W2zwrVtg
     * @param tweetUrl
     * @return
     */
    public static String getTweetId(String tweetUrl) {
        if (StringUtils.isBlank(tweetUrl)) {
            return null;
        }
        String t = StringUtils.substringAfter(tweetUrl, "/status/");
        return StringUtils.substringBefore(t, "?");
    }

    /**
     * 判断是否最后一页. 如果最后一页的数据量小于50
     * @param response
     * @return true 最后一页, false还有下一页
     */
    public static Boolean isLastPagination(TwitterResponse response) {
        if (response == null) {
            return Boolean.TRUE;
        }
        if (response.getData() instanceof List) {
            List data = (List) response.getData();
            if (CollectionUtils.isEmpty( data)) {
                return Boolean.TRUE;
            }

            if (data.size() < 50) {
                return Boolean.TRUE;
            }

            // data售后为空, next_token不存在, 集合数据无数小于50(这个50是拍脑袋的,
            // twitter每页返回的数量不等, 并不是按照max_results定义的返回
            if (StringUtils.isNotBlank(response.getMeta().getNext_token())) {
                return Boolean.FALSE;
            }
        }

        return Boolean.TRUE;
    }
}
