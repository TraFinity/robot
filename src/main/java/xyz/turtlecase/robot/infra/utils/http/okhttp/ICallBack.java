package xyz.turtlecase.robot.infra.utils.http.okhttp;

import okhttp3.Call;

public interface ICallBack {
    /**
     * 请示成功后的处理
     *
     * @param call
     * @param data
     */
    void onSuccessful(Call call, String data);

    /**
     * 请示失败后的处理
     *
     * @param call
     * @param errorMsg
     */
    void onFailure(Call call, String errorMsg);
}
