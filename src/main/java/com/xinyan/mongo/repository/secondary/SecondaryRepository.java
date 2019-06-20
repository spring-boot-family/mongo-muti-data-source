package com.xinyan.mongo.repository.secondary;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface SecondaryRepository extends MongoRepository<SecondaryObject, String> {
}
