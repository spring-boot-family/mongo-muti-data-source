package com.xinyan.mongo.config.mongo;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.xinyan.mongo.repository.primary", mongoTemplateRef = PrimaryMongoConfig.MONGO_TEMPLATE)
public class PrimaryMongoConfig {
    public static final String MONGO_TEMPLATE = "primaryMongoTemplate";
}
