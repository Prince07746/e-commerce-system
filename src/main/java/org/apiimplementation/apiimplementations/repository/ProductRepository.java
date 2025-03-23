package org.apiimplementation.apiimplementations.repository;

import org.apiimplementation.apiimplementations.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductRepository extends MongoRepository<Product,String> {

}
