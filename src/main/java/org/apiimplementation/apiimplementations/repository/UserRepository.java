package org.apiimplementation.apiimplementations.repository;

import org.apiimplementation.apiimplementations.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<User,String> {

}
