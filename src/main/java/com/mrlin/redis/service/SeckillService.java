package com.mrlin.redis.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.mrlin.commons.domain.ResultInfo;
import com.mrlin.commons.domain.ResultResponse;
import com.mrlin.commons.exception.ParameterException;
import com.mrlin.redis.lock.RedissonDistributedLocker;
import com.mrlin.redis.po.SeckillVouchers;
import com.mrlin.redis.po.SignInDinerInfo;
import com.mrlin.redis.po.VoucherOrders;
import com.mrlin.redis.util.AssertUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrlin.commons.RedisKeyConstant;
import org.springframework.transaction.interceptor.TransactionAspectSupport;


import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 秒杀业务逻辑层
 */
@Service
public class SeckillService {


    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonDistributedLocker redissonDistributedLocker;

    /**
     * 添加需要抢购的代金券
     *
     * @param seckillVouchers
     */
    @Transactional(rollbackFor = Exception.class)
    public void addSeckillVouchers(SeckillVouchers seckillVouchers) {


        Date now = new Date();

        //采用redis实现，把秒杀活动的代金劵放入redis
        String key = RedisKeyConstant.seckill_vouchers.getKey() +
                seckillVouchers.getFkVoucherId();
        //验证redis中是否存在该劵的秒杀活动(用hash不涉及序列化；操作比较快)
        Map<String,Object> map = redisTemplate.opsForHash().entries(key);

        //插入redis
        seckillVouchers.setIsValid(1);
        seckillVouchers.setCreateDate(now);
        seckillVouchers.setUpdateDate(now);

        redisTemplate.opsForHash().putAll(key,BeanUtil.beanToMap(seckillVouchers));
        
    }

    public ResultInfo doSeckill(Integer voucherId) {

        //采用redis
        String key = RedisKeyConstant.seckill_vouchers.getKey()+voucherId;
        Map<String,Object> map = redisTemplate.opsForHash().entries(key);
        AssertUtil.isNotEmpty(map,"该商品未参加秒杀活动");
        //map 转对象
        SeckillVouchers seckillVouchers = BeanUtil.mapToBean(map, SeckillVouchers.class, true, null);


        // 判断是否开始、结束
        Date now = new Date();
        AssertUtil.isTrue(now.before(seckillVouchers.getStartTime()), "该抢购还未开始");
        AssertUtil.isTrue(now.after(seckillVouchers.getEndTime()), "该抢购已结束");
        // 判断是否卖完
        AssertUtil.isTrue(seckillVouchers.getAmount() < 1, "======该券已经卖完了");


        //获取Redissson 分布式锁
        String lockName = RedisKeyConstant.lock_key.getKey()+voucherId;
        //Redisson 分布式锁处理
        RLock rLock =   redissonDistributedLocker.lock(lockName,6000);

        try {
                 //扣库存
                 Integer count = (Integer)redisTemplate.opsForHash().get(key,"amount");
                 System.out.println("当前库存："+count);
                 AssertUtil.isTrue(count==0,"加锁后该券已经卖完了");
                 redisTemplate.opsForHash().put(key,"amount",count-1);
                 // 获取登录用户信息
//                 long count = redisTemplate.opsForHash().increment(key,"amount",-1);
//                 System.out.println("当前库存："+count);
                 AssertUtil.isTrue(count==0,"该券已经卖完了");
                 SignInDinerInfo dinerInfo = new SignInDinerInfo();
                 dinerInfo.setId(1);
                 dinerInfo.setUsername("ljm");
                 dinerInfo.setNickname("cctv");
                 //下单
                 VoucherOrders voucherOrders = new VoucherOrders();
                 voucherOrders.setFkDinerId(dinerInfo.getId());
                 voucherOrders.setFkVoucherId(seckillVouchers.getFkVoucherId());
                 String orderNo = IdUtil.getSnowflake(1, 1).nextIdStr();
                 voucherOrders.setOrderNo(orderNo);
                 voucherOrders.setOrderType(1);
                 voucherOrders.setStatus(0);
                 //商品id+ 用户id
                 //        String ordersKey = RedisKeyConstant.voucher_orders.getKey() +
                 //                seckillVouchers.getFkVoucherId()+":"+dinerInfo.getId();
                 //        Map<String,Object> orderskMap = redisTemplate.opsForHash().entries(ordersKey);
                 //        AssertUtil.isEmpty(orderskMap,"该用户，以参加抢购活动");
                 String ordersKey = RedisKeyConstant.voucher_orders.getKey()+dinerInfo.getId()+orderNo;
                 redisTemplate.opsForHash().putAll(ordersKey,BeanUtil.beanToMap(voucherOrders));

        }catch (Exception e){
            e.printStackTrace();
            if(e instanceof ParameterException){
                return ResultResponse.buildError(0,"该劵已经卖完了");
            }
        }
        finally {
            redissonDistributedLocker.unlock(rLock);
        }

        return ResultResponse.buildSuccess( "抢购成功");
    }
}
