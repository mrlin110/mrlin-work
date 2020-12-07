package com.mrlin.commons.domain;
/**
 * @author wmy
 * @create 2020-11-21 11:31
 */



/**
 * 公共返回对象工具类
 */
public class ResultResponse {
    // 成功
    public static final int SUCCESS_CODE = 1;
    // 成功提示信息
    public static final String SUCCESS_MESSAGE = "Successful.";
    // 错误
    public static final int ERROR_CODE = 0;
    // 未登录
    public static final int NO_LOGIN_CODE = -100;
    // 请登录提示信息
    public static final String NO_LOGIN_MESSAGE = "Please login.";
    // 错误提示信息
    public static final String ERROR_MESSAGE = "Oops! Something was wrong.";
    /**
     * 请求出错返回
     *
     * @param <T>
     * @return
     */
    public static <T> ResultInfo<T> buildError() {
        ResultInfo<T> resultInfo = build(ERROR_CODE,
                ERROR_MESSAGE,null);
        return resultInfo;
    }

    /**
     * 请求出错返回
     *
     * @param errorCode 错误代码
     * @param message   错误提示信息
     * @param <T>
     * @return
     */
    public static <T> ResultInfo<T> buildError(int errorCode, String message) {
        ResultInfo<T> resultInfo = build(errorCode, message, null);
        return resultInfo;
    }

    /**
     * 请求成功返回
     *
     * @param <T>
     * @return
     */
    public static <T> ResultInfo<T> buildSuccess() {
        ResultInfo<T> resultInfo = build(SUCCESS_CODE,
                SUCCESS_MESSAGE, null);
        return resultInfo;
    }

    /**
     * 请求成功返回
     *
     * @param data 返回数据对象
     * @param <T>
     * @return
     */
    public static <T> ResultInfo<T> buildSuccess( T data) {
        ResultInfo<T> resultInfo = build(SUCCESS_CODE,
                SUCCESS_MESSAGE, data);
        return resultInfo;
    }

    /**
     * 构建返回对象方法
     *
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResultInfo<T> build(Integer code, String message, T data) {
        if (code == null) {
            code = SUCCESS_CODE;
        }
        if (message == null) {
            message = SUCCESS_MESSAGE;
        }
        ResultInfo resultInfo = new ResultInfo();
        resultInfo.setCode(code);
        resultInfo.setMessage(message);
        resultInfo.setData(data);
        return resultInfo;
    }
}
