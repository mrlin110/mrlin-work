package com.mrlin.redis.util;

import cn.hutool.core.util.StrUtil;
import com.mrlin.commons.exception.ParameterException;
import org.springframework.util.ObjectUtils;


/**
 * 断言工具类
 */
public class AssertUtil {
    // 错误提示信息
    public static final String ERROR_MESSAGE = "Oops! Something was wrong.";
    /**
     * 判断非空
     *
     * @param object
     * @param message
     */
    public static void isNotEmpty(Object object, String... message) {
        if (ObjectUtils.isEmpty(object)) {
            execute(message);
        }
    }
    /**
     * 判断是否不为空
     *
     * @param object
     * @param message
     */
    public static void isEmpty(Object object, String... message) {
        if (!ObjectUtils.isEmpty(object)) {
            execute(message);
        }
    }

    /**
     * 判断对象非空
     *
     * @param obj
     * @param message
     */
    public static void isNotNull(Object obj, String... message) {
        if (obj == null) {
            execute(message);
        }
    }

    /**
     * 判断结果是否为真
     *
     * @param isTrue
     * @param message
     */
    public static void isTrue(boolean isTrue, String... message) {
        if (isTrue) {
            execute(message);
        }
    }

    /**
     * 最终执行方法
     *
     * @param message
     */
    private static void execute(String... message) {
        String msg = ERROR_MESSAGE;
        if (message != null && message.length > 0) {
            msg = message[0];
        }
        throw new ParameterException(msg);
    }

}