package com.mrlin.redis.service;

import com.mrlin.commons.domain.ResultInfo;
import com.mrlin.commons.exception.ParameterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SeckillServiceTest {

    private static final String VOUCHER_KEY = "seckill_vouchers:9";
    private static final String ORDER_KEY = "voucher_orders:1";

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private HashOperations hashOperations;

    @InjectMocks
    private SeckillService seckillService;

    @Before
    public void setUp() {
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.entries(VOUCHER_KEY)).thenReturn(activeVoucher(3));
    }

    @Test
    public void firstPurchaseSavesOrderAndDecrementsStockOnce() {
        when(hashOperations.putIfAbsent(eq(ORDER_KEY), eq("orderNo"), anyString())).thenReturn(true);
        when(hashOperations.increment(VOUCHER_KEY, "amount", -1L)).thenReturn(2L);

        ResultInfo result = seckillService.doSeckill(9);

        assertEquals(Integer.valueOf(1), result.getCode());
        verify(hashOperations).increment(VOUCHER_KEY, "amount", -1L);
        ArgumentCaptor<Map> order = ArgumentCaptor.forClass(Map.class);
        verify(hashOperations).putAll(eq(ORDER_KEY), order.capture());
        assertEquals(Integer.valueOf(9), order.getValue().get("fkVoucherId"));
        assertEquals(Integer.valueOf(1), order.getValue().get("fkDinerId"));
        verify(redisTemplate, never()).delete(ORDER_KEY);
    }

    @Test
    public void repeatPurchaseDoesNotConsumeStock() {
        when(hashOperations.putIfAbsent(eq(ORDER_KEY), eq("orderNo"), anyString())).thenReturn(false);

        try {
            seckillService.doSeckill(9);
        } catch (ParameterException ex) {
            assertEquals("该用户，以参加抢购活动", ex.getMessage());
            verify(hashOperations, never()).increment(any(), any(), anyLong());
            verify(hashOperations, never()).putAll(any(), any());
            return;
        }
        throw new AssertionError("expected ParameterException");
    }

    @Test
    public void soldOutDecrementIsRolledBack() {
        when(hashOperations.putIfAbsent(eq(ORDER_KEY), eq("orderNo"), anyString())).thenReturn(true);
        when(hashOperations.increment(VOUCHER_KEY, "amount", -1L)).thenReturn(-1L);

        try {
            seckillService.doSeckill(9);
        } catch (ParameterException ex) {
            assertEquals("该券已经卖完了", ex.getMessage());
            verify(hashOperations).increment(VOUCHER_KEY, "amount", 1L);
            verify(redisTemplate).delete(ORDER_KEY);
            verify(hashOperations, never()).putAll(any(), any());
            return;
        }
        throw new AssertionError("expected ParameterException");
    }

    private static Map<String, Object> activeVoucher(int amount) {
        Map<String, Object> voucher = new HashMap<String, Object>();
        voucher.put("fkVoucherId", 9);
        voucher.put("amount", amount);
        voucher.put("isValid", 1);
        voucher.put("startTime", new Date(System.currentTimeMillis() - 60_000L));
        voucher.put("endTime", new Date(System.currentTimeMillis() + 3_600_000L));
        return voucher;
    }
}
