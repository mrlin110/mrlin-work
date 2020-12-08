package com.mrlin.redis.controller;




import com.mrlin.commons.domain.ResultInfo;
import com.mrlin.commons.domain.ResultResponse;
import com.mrlin.redis.po.BaseModel;
import com.mrlin.redis.po.SeckillVouchers;
import com.mrlin.redis.service.SeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 秒杀控制层
 */
@RestController
@RequestMapping("/seckill")
@Api(tags = "秒杀接口")
public class SeckillController {

    @Resource
    private SeckillService seckillService;


    /**
     * 秒杀下单
     *
     * @param voucherId
     * @return
     */
    @PostMapping("{voucherId}")
    @ApiOperation(value = "秒杀下单")
    public ResultInfo<String> doSeckill(@PathVariable Integer voucherId) {
        ResultInfo resultInfo = seckillService.doSeckill(voucherId);
        return resultInfo;
    }


    /**
     * 新增秒杀活动
     *
     * @param seckillVouchers
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "添加秒杀活动")
    public ResultInfo<String> addSeckillVouchers(@RequestBody SeckillVouchers seckillVouchers) {
        seckillService.addSeckillVouchers(seckillVouchers);
        return ResultResponse.buildSuccess(
                "添加成功");
    }
    /**
     * 新增秒杀活动
     *
     * @param
     * @return
     */
    @PostMapping("/test")
    @ApiOperation(value = "测试")
    public ResultInfo<String> addSeckillVouchers(@RequestBody BaseModel input) {
        System.out.println(input);
        return ResultResponse.buildSuccess(
                "添加成功");
    }
}
