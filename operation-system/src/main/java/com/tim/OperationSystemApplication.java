package com.tim;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * @description: 运营系统程序入口
 * @author: lizhiming
 * @create: 2018/4/20 15:30
 */
//开启Feign的功能
@EnableFeignClients
@EnableDiscoveryClient
//@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class}) springboot启动时会自动注入数据源和配置jpa
@SpringBootApplication(exclude={DataSourceAutoConfiguration.class,HibernateJpaAutoConfiguration.class})
@Slf4j
public class OperationSystemApplication {

	/**
	 * @EnableFeignClients注解开启扫描Spring Cloud Feign客户端的功能
	 * @EnableDiscoveryClient能激活Eureka中的DiscoveryClient实现，才能实现Controller中对服务信息的输出
	 */
	public static void main(String[] args) {
		SpringApplication.run(OperationSystemApplication.class, args);
		log.info("【运营系统】启动成功！！！");
	}
}
