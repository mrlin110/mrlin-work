package com.mrlin.commons;



public enum RedisKeyConstant {

    verify_code("verify_code:", "验证码"),

    seckill_vouchers("seckill_vouchers:","秒杀劵的key"),
    voucher_orders("voucher_orders:","下单的key"),
    lock_key("lockBy:","分布式锁的key")

    ;


    private String key;
    private String desc;

    RedisKeyConstant(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public String getKey() {
        return key;
    }
    public String getDesc() {
        return desc;
    }

}