package com.xinyan.mongo.controller;

import com.xinyan.mongo.repository.primary.PrimaryObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/mongo")
public class MongoController {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    @Qualifier("secondaryMongoTemplate")
    MongoTemplate secondaryMongoTemplate;

    @PostMapping("/primary/save")
    public String primary() {
        return mongoTemplate.save(new PrimaryObject(null, ""), "primary").getId();
    }

    @PostMapping("/secondary/save")
    public String secondary() {
        return secondaryMongoTemplate.save(new PrimaryObject(null, ""), "secondary").getId();
    }

}
