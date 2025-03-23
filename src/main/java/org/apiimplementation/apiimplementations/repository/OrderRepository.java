package org.apiimplementation.apiimplementations.repository;

import org.apiimplementation.apiimplementations.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface OrderRepository extends MongoRepository<Order,String> {

}
