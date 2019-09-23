package com.xinyan.mongo.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.xinyan.mongo.config.mongo.PrimaryMongoConfig;
import com.xinyan.mongo.config.mongo.SecondaryMongoConfig;
import com.xinyan.mongo.config.properties.MultipleMongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.StringUtils;

@Configuration
public class MultiplyMongoConfig {

    @Value("${mongodb.connectionsPerHost}")
    private Integer connectionsPerHost;
    @Value("${mongodb.connectTimeout}")
    private Integer connectTimeout;
    @Value("${mongodb.socketTimeout}")
    private Integer socketTimeout;
    @Value("${mongodb.threadsAllowedToBlockForConnectionMultiplier}")
    private Integer threadsAllowedToBlockForConnectionMultiplier;

    @Autowired
    private MultipleMongoProperties multipleMongoProperties;

    /**
     * 注入的MongoTemplate必须有一个添加@Primary注解
     */
    @Primary
    @Bean(name = PrimaryMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate primaryMongoTemplate() {
        MongoDbFactory mongoDbFactory = buildMongoDbFactory(multipleMongoProperties.getPrimary());
        return new MongoTemplate(mongoDbFactory, newMappingMongoConverter(mongoDbFactory));
    }

    @Bean(SecondaryMongoConfig.MONGO_TEMPLATE)
    public MongoTemplate secondaryMongoTemplate() {
        MongoDbFactory mongoDbFactory = buildMongoDbFactory(multipleMongoProperties.getSecondary());
        return new MongoTemplate(mongoDbFactory, newMappingMongoConverter(mongoDbFactory));
    }

    private MappingMongoConverter newMappingMongoConverter(MongoDbFactory mongoDbFactory) {
        //移除"_class"字段
        MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), new MongoMappingContext());
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        // key包含点'.',使用下划线'_'代替
        converter.setMapKeyDotReplacement("_");
        return converter;
    }

    public MongoDbFactory buildMongoDbFactory(MongoProperties mongoProperties) {
        return newSimpleMongoDbFactory(mongoProperties);
    }

    private MongoDbFactory newSimpleMongoDbFactory(MongoProperties mongoProperties) {
        // MongoClient配置信息
        MongoClientOptions mongoClientOptions = MongoClientOptions.builder()
                // 连接池连接数
                .connectionsPerHost(connectionsPerHost)
                // 连接超时时间
                .connectTimeout(connectTimeout)
                // socket 套接字超时时间
                .socketTimeout(socketTimeout)
                // 最大创建的线程数量为threadsAllowedToBlockForConnectionMultiplier * connectionsPerHost
                .threadsAllowedToBlockForConnectionMultiplier(threadsAllowedToBlockForConnectionMultiplier)
                .build();
        // 同一个数据库可以重用同一个MongoClient
        MongoClient mongoClient = null;
        if (StringUtils.isEmpty(mongoProperties.getUsername()) || StringUtils.isEmpty(mongoProperties.getPassword())) {
            mongoClient = new MongoClient(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()), mongoClientOptions);
        } else {
            // 数据库账号、密码连接
            MongoCredential credential = MongoCredential.createCredential(mongoProperties.getUsername(), mongoProperties.getDatabase(), mongoProperties.getPassword());
            mongoClient = new MongoClient(new ServerAddress(mongoProperties.getHost(), mongoProperties.getPort()), credential, mongoClientOptions);
        }
        return new SimpleMongoDbFactory(mongoClient, mongoProperties.getDatabase());
    }

}
