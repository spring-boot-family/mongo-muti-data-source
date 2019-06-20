package com.xinyan.mongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

/**
 * 启动类
 *
 * @author weimin_ruan
 * @date 2019/6/20
 */
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class MongoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MongoApplication.class, args);
	}

}
