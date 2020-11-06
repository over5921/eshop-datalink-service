package com.roncoo.eshop.datalink;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
如果nginx本地，走nginx local cache没有，在本机房的通过twemproxy读本机房的从集群，如果还是没有，则发送http请求给数据直连服务

数据直连服务，先在自己本地读取ehcache（有的时候也可以忽略，我这里就不做了，因为之前已经做过了），读redis主集群，通过fegion拉取依赖服务的接口

将数据写入主集群中，主集群会同步到各个机房的从集群，同时数据直连服务将获取到的数据返回给nginx，nginx会写入自己本地的local cache
 * @author 41241
 *
 */
@SpringBootApplication(exclude =DataSourceAutoConfiguration.class)
@EnableEurekaClient
@EnableFeignClients
public class EshopDataLinkServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EshopDataLinkServiceApplication.class, args); 
	}
	
	@Bean
	public JedisPool jedisPool() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(100);
		config.setMaxIdle(5);
		config.setMaxWaitMillis(1000 * 10); 
		config.setTestOnBorrow(true);
		return new JedisPool(config, "192.168.101.105", 1111);
	}
	
}
