package com.mrlin.config;


import io.micrometer.core.instrument.util.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * redisson装配各种模式
 * @author Guoqing.Lee
 * @date 2019年1月23日 下午3:14:07
 *
 */
@Configuration
@ConditionalOnClass(Config.class)
public class RedissonAutoConfiguration {

    @Autowired
    private RedisProperties redssionProperties;

    /**
     * @description:简单模式配置Redisson
     * @date: 2020/5/28 0028 15:03
     * @author: xpy
     */
    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode",havingValue = "simple",matchIfMissing = true)
    public RedissonClient simpleRedisson() {
        //单节点配置
        Config config = new Config();
        SingleServerConfig singleServerConfig = config.useSingleServer();
        if(StringUtils.isNotBlank(redssionProperties.getUrl())){
            singleServerConfig.setAddress(redssionProperties.getUrl());
        }else {
            singleServerConfig.setAddress("redis://"+redssionProperties.getHost()+":"+redssionProperties.getPort());
        }
        singleServerConfig.setDatabase(redssionProperties.getDatabase());
        if (StringUtils.isNotBlank(redssionProperties.getPassword())) {
            singleServerConfig.setPassword(redssionProperties.getPassword());
        }
//        singleServerConfig.setTimeout(1000);
//        singleServerConfig.setRetryAttempts(3);
//        singleServerConfig. setRetryInterval(1000);
//        //**此项务必设置为redisson解决之前bug的timeout问题关键*****
        singleServerConfig.setPingConnectionInterval(1000);
//        singleServerConfig.setDatabase(0);
        return Redisson.create(config);
    }

    /**
     * @description:sentinel 模式 Redisson
     * @date: 2020/5/28 0028 15:03
     * @author: xpy
     */
    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode",havingValue = "sentinel")
    public RedissonClient sentinelRedisson() {
        System.out.println("sentinel redssionProperties:" + redssionProperties.getSentinel());
        Config config = new Config();
        List<String> nodes = redssionProperties.getSentinel().getNodes();
        List<String> newNodes = new ArrayList(nodes.size());
        nodes.forEach((index) -> newNodes.add(index.startsWith("redis://") ? index : "redis://" + index));


        SentinelServersConfig serverConfig = config.useSentinelServers()
                .addSentinelAddress(newNodes.toArray(new String[0]))
                .setMasterName(redssionProperties.getSentinel().getMaster())
                .setReadMode(ReadMode.SLAVE);
//                .setFailedAttempts(redssionProperties.getSentinel().getFailMax())
//                .setTimeout(redssionProperties.getTimeout())
//                .setMasterConnectionPoolSize(redssionProperties.getPool().getSize())
//                .setSlaveConnectionPoolSize(redssionProperties.getPool().getSize());

        if (StringUtils.isNotBlank(redssionProperties.getPassword())) {
            serverConfig.setPassword(redssionProperties.getPassword());
        }

        return Redisson.create(config);
    }


    /**
     * @description: 集群模式分布式锁
     * @date: 2020/8/10 0010 11:14
     * @author: xpy
     */
    @Bean
    @ConditionalOnProperty(value = "spring.redis.mode",havingValue = "cluster")
    public RedissonClient clusterRedisson() {
        Config config = new Config();
        List<String> nodes = redssionProperties.getCluster().getNodes();
        List<String> newNodes = new ArrayList(nodes.size());
        nodes.forEach((index) -> newNodes.add(index.startsWith("redis://") ? index : "redis://" + index));
        ClusterServersConfig serverConfig = config.useClusterServers()
                .addNodeAddress(newNodes.toArray(new String[0]));
        if (StringUtils.isNotBlank(redssionProperties.getPassword())) {
            serverConfig.setPassword(redssionProperties.getPassword());
        }
        return Redisson.create(config);
    }
    

}
