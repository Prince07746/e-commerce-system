package org.apiimplementation.apiimplementations.service;

import org.apiimplementation.apiimplementations.model.Order;
import org.apiimplementation.apiimplementations.model.Payment;
import org.apiimplementation.apiimplementations.model.Product;
import org.apiimplementation.apiimplementations.model.status.PaymentStatus;
import org.apiimplementation.apiimplementations.model.status.Status;
import org.apiimplementation.apiimplementations.repository.PaymentRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PaymentService {

    private  final PaymentRepository paymentRepository;
    private final MongoTemplate mongoTemplate;
    private final ProductService productService;
    private final OrderService orderService;
    private final UserService userService;


    public PaymentService(
            PaymentRepository paymentRepository,
            MongoTemplate mongoTemplate,
            ProductService productService,
            OrderService orderService,
            UserService userService
    ){
        this.paymentRepository = paymentRepository;
        this.mongoTemplate = mongoTemplate;
        this.productService = productService;
        this.orderService = orderService;
        this.userService = userService;
    }


    public void confirmOrder(String orderId) throws InsufficientResourcesException {
        Order order = orderService.getOrder(orderId);
        if (order != null) {

            for (Map.Entry<String ,Integer> product : order.getProductList().entrySet()) {
                Product stockProduct = productService.getProduct(product.getKey());
                if ( !(stockProduct.getStockQuantity() - product.getValue() > -1) ) {
                    throw new InsufficientResourcesException("Insufficient product ID: "+product.getKey());
                }
            }
            // perform payment
            if(1000 > order.getTotalAmount()){
                addPurchase(order);
            }

        }
    }


    private void addPurchase(Order order){
        // update order status
        order.setStatus(Status.CONFIRMED);
        orderService.updateOrder(order);

        // update user loyalty points
        userService.getUser(order.getUserId()).ifPresent((user)->{
            user.setLoyaltyPoints(
                    (Integer.parseInt(String.valueOf(order.getTotalAmount()+user.getLoyaltyPoints())))
            );
            userService.updateUser(user);
        });

        // update stock quantity
        for (Map.Entry<String,Integer> product : order.getProductList().entrySet()) {
            Product stockProduct = productService.getProduct(product.getKey());
            stockProduct.setStockQuantity(stockProduct.getStockQuantity() - product.getValue());
            productService.updateProduct(stockProduct);
        }
        paymentRepository.save(new Payment(order.getId(), order.getUserId(), order.getTotalAmount(),
                PaymentStatus.COMPLETED,
                LocalDateTime.now()));
    }


    public void cancelOrder(String orderId){

        Payment payment = paymentRepository.findByOrderId(orderId);
        payment.setPaymentStatus(PaymentStatus.FAILED);
        updatePayment(payment);

        // If an order is cancelled, the products must be re-stocked.
        for (Map.Entry<String,Integer>product : orderService.getOrder(payment.getOrderId()).getProductList().entrySet()) {
            Product stockProduct = productService.getProduct(product.getKey());
            stockProduct.setStockQuantity(stockProduct.getStockQuantity() + product.getValue());
            productService.updateProduct(stockProduct);
        }

        Order order = orderService.getOrder(orderId);
        order.setStatus(Status.CANCELLED);
        orderService.updateOrder(order);

    }




    // GET
    public List<Payment> getPaymentList(){
       return paymentRepository.findAll();
    }
    public List<Payment> getPaymentListUser(String userId){
        return paymentRepository.findAll().stream().filter(payment -> payment.getUserId().equals(userId)).toList();
    }


    // updating payment
    public Update updatePaymentFormat(Payment payment){
        return  new Update()
                .set("orderId",payment.getId())
                .set("userId",payment.getUserId())
                .set("amount",payment.getAmount())
                .set("paymentStatus",payment.getPaymentStatus())
                .set("paymentDate",payment.getPaymentDate());
    }

    public void updatePayment(Payment payment){
        Query query = new Query(Criteria.where("orderId").is(payment.getOrderId()));
        mongoTemplate.updateFirst(query,updatePaymentFormat(payment), Payment.class);
    }


}