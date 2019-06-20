package com.xinyan.mongo.repository.primary;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrimaryRepository extends MongoRepository<PrimaryObject, String> {
}
