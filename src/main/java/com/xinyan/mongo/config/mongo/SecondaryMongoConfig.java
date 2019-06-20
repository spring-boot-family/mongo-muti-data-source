package com.xinyan.mongo.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.xinyan.mongo.repository.secondary", mongoTemplateRef = SecondaryMongoConfig.MONGO_TEMPLATE)
public class SecondaryMongoConfig {
    public static final String MONGO_TEMPLATE = "secondaryMongoTemplate";
}
