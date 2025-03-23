package org.apiimplementation.apiimplementations.repository;

import org.apiimplementation.apiimplementations.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface PaymentRepository extends MongoRepository<Payment,String> {

    Payment findByOrderId(String orderId);
}
