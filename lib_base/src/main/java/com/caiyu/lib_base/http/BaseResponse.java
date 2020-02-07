package com.caiyu.lib_base.http;

/**
 * @author luys
 * @describe
 * @date 2019/4/15
 * @email ltym_lys@126.com
 */
public class BaseResponse<T> {
    private int code;
    private String msg;
    private String time;
    private T data;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
