package xyz.turtlecase.robot.infra.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.Arrays;

@Data
public class ResultData {
    /**
     * 成功失败标记位
     */
    private boolean success;
    /**
     * 成功失败标记 success/failed
     */
    private String state;
    /**
     * 状态码, 同http status
     *
     * @see org.springframework.http.HttpStatus
     */
    private Integer statusCode;
    /**
     * 返回的数据
     */
    private String data;
    /**
     * 返回的消息
     */
    private String message;
    /**
     * 文件流
     */
    private byte[] fileStream;

    public ResultData() {
    }

    public ResultData(Boolean success) {
        this.success = success;
    }

    public JSONObject toJson() {
        return (JSONObject) JSONObject.toJSON(this);
    }

}
