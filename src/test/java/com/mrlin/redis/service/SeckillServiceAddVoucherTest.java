package com.mrlin.redis.service;

import com.mrlin.redis.po.SeckillVouchers;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Adding a voucher only writes Redis. The application has no transaction
 * manager, so the method must still persist when Spring transaction
 * management is enabled.
 */
public class SeckillServiceAddVoucherTest {

    @Test
    public void addSeckillVouchersPersistsWithoutTransactionManager() {
        RedisTemplate redisTemplate = mock(RedisTemplate.class);
        HashOperations hashOperations = mock(HashOperations.class);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(any())).thenReturn(Collections.emptyMap());

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(Config.class);
        context.registerBean("redisTemplate", RedisTemplate.class, () -> redisTemplate);
        context.refresh();
        try {
            SeckillVouchers voucher = new SeckillVouchers();
            voucher.setFkVoucherId(7);
            voucher.setAmount(5);
            Date start = new Date(System.currentTimeMillis() - 60_000L);
            Date end = new Date(System.currentTimeMillis() + 3_600_000L);
            voucher.setStartTime(start);
            voucher.setEndTime(end);

            context.getBean(SeckillService.class).addSeckillVouchers(voucher);

            verify(hashOperations).putAll(eq("seckill_vouchers:7"), any(Map.class));
        } finally {
            context.close();
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class Config {
        @Bean
        public SeckillService seckillService() {
            return new SeckillService();
        }
    }
}
