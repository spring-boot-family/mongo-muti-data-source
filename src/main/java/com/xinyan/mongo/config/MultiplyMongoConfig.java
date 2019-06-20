package com.xinyan.mongo.config;

import com.mongodb.MongoClient;
import com.xinyan.mongo.config.mongo.PrimaryMongoConfig;
import com.xinyan.mongo.config.mongo.SecondaryMongoConfig;
import com.xinyan.mongo.config.properties.MultipleMongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class MultiplyMongoConfig {

    @Autowired
    private MultipleMongoProperties multipleMongoProperties;

    /**
     * 注入的MongoTemplate必须有一个添加@Primary注解
     */
    @Primary
    @Bean(name = PrimaryMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate primaryMongoTemplate() {
        return new MongoTemplate(mongoDbFactory(multipleMongoProperties.getPrimary()));
    }

    @Bean(SecondaryMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate secondaryMongoTemplate() {
        return new MongoTemplate(mongoDbFactory(multipleMongoProperties.getSecondary()));
    }

    private MongoDbFactory mongoDbFactory(MongoProperties properties) {
        return new SimpleMongoDbFactory(new MongoClient(properties.getHost(), properties.getPort()), properties.getDatabase());
    }

    /*@Primary
    @Bean
    public MongoDbFactory primaryFactory(MongoProperties properties) {
        return new SimpleMongoDbFactory(new MongoClient(properties.getHost(), properties.getPort()), properties.getDatabase());
    }*/

    /*@Bean
    public MongoDbFactory secondaryFactory(MongoProperties properties) {
        return new SimpleMongoDbFactory(new MongoClient(properties.getHost(), properties.getPort()), properties.getDatabase());
    }*/
}
