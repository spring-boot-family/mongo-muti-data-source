package com.xinyan.mongo;

import com.xinyan.mongo.repository.primary.PrimaryObject;
import com.xinyan.mongo.repository.primary.PrimaryRepository;
import com.xinyan.mongo.repository.secondary.SecondaryObject;
import com.xinyan.mongo.repository.secondary.SecondaryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MongoApplicationTests {

	@Autowired
	private PrimaryRepository primaryRepository;

	@Autowired
	private SecondaryRepository secondaryRepository;

	@Autowired
    private MongoTemplate primaryMongoTemplate;

    @Autowired
    @Qualifier("secondaryMongoTemplate")
    private MongoTemplate secondaryMongoTemplate;

	@Test
	public void contextLoads() {
        System.out.println("************************************************************");
        System.out.println("测试开始");
        System.out.println("************************************************************");

        PrimaryObject primaryObject = primaryRepository.save(new PrimaryObject(null, String.valueOf(Math.random())));
        System.out.println(primaryObject);
        SecondaryObject secondaryObject = secondaryRepository.save(new SecondaryObject(null, String.valueOf(Math.random())));
        System.out.println(secondaryObject);

        PrimaryObject object = primaryMongoTemplate.save(new PrimaryObject(null, String.valueOf(Math.random())));
        System.out.println(object);
        SecondaryObject object2 = secondaryMongoTemplate.save(new SecondaryObject(null, String.valueOf(Math.random())));
        System.out.println(object2);


        System.out.println("************************************************************");
        System.out.println("测试完成");
        System.out.println("************************************************************");
	}

}
