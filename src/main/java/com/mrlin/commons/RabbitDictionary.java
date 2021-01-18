package com.mrlin.commons;

/**
 * @Description TODO
 * @Author linjb
 * @Date 2020/2/17 0017 11:24
 * @Version 1.0
 */
public enum RabbitDictionary {
    fanout("fanout","共享资源"),
    direct("direct","指定资源"),


    ;

    private String code;
    private String description;

    private RabbitDictionary(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
