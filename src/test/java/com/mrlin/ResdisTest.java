package com.mrlin;


import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ResdisTest {

    @Resource
	private  RedisTemplate redisTemplate;

	@Test
	public void pong() throws Exception {
         String pong = redisTemplate.getConnectionFactory().getConnection().ping();
		System.out.println("pong: "+ pong);
	}
	

	
	
	
}
