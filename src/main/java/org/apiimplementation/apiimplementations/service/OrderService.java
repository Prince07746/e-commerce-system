package org.apiimplementation.apiimplementations.service;

import org.apiimplementation.apiimplementations.components.DataLoader;
import org.apiimplementation.apiimplementations.error.customError.InsufficientProductException;
import org.apiimplementation.apiimplementations.error.customError.NotFoundException;
import org.apiimplementation.apiimplementations.model.status.Status;
import org.apiimplementation.apiimplementations.repository.OrderRepository;
import org.apiimplementation.apiimplementations.model.Order;
import org.apiimplementation.apiimplementations.model.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final MongoTemplate mongoTemplate;
    private final ProductService productService;
    private final UserService userService;

    public OrderService(OrderRepository orderRepo, MongoTemplate mongoTemplate, ProductService productService,UserService userService) {
        this.orderRepo = orderRepo;
        this.mongoTemplate = mongoTemplate;
        this.productService = productService;
        this.userService = userService;
    }


    // CREATE and ADD (ORDER,PRODUCT)
    public void addOrder(Order order) {
        if (order == null || order.getUserId() == null) {
            throw new IllegalArgumentException("Order details are incomplete.");
        }
        order.setStatus(Status.PENDING);
        double totalAmount = 0.0;
        for(Map.Entry<String,Integer> productOder:order.getProductList().entrySet()){
            Product product = productService.getProduct(productOder.getKey());
            if(product != null){
                totalAmount += product.getPrice()*productOder.getValue();
            }
        }
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDate.now());
        orderRepo.save(order);
    }


    public void addProductToNewOrder(String productId, int quantity, String userId) {
        Product product = productService.getProduct(productId);
        if(userService.getUser(userId).isPresent() && product != null){
            Order order = new Order();
            order.setProductList(new HashMap<String, Integer>());
            order.setUserId(userId);
            order.setStatus(Status.PENDING);
            order.setOrderDate(LocalDate.now());
            order.setId(DataLoader.generateValidId());

            Map<String,Integer> productList = order.getProductList();
            addProductToOrderHelper(quantity, order, productList, product);
        }else{
            throw new NotFoundException("User or Product not found");
        }
    }


    private void addProductToOrderHelper(int quantity, Order order, Map<String,Integer> productList, Product product) {
        if (product != null && product.getStockQuantity() - quantity > -1) {
            // add product to order product list
            productList.put(product.getId(),quantity);
            order.setProductList(productList);
            double totalAmount = 0.0;
            for(Map.Entry<String,Integer> productOder:order.getProductList().entrySet()){
                Product product1 = productService.getProduct(productOder.getKey());
                if(product1 != null){
                    totalAmount += product.getPrice()*productOder.getValue();
                }
            }
            order.setTotalAmount(totalAmount);

            if(getOrder(order.getId()) != null){
                updateOrder(order);
            }else{
                addOrder(order);
            }
        } else {
            throw new InsufficientProductException("no product or stock sufficient available ");
        }
    }


    public void addProductToOrder(String productId, int quantity, String orderId) {
        Order order = getOrder(orderId);
        Product product = productService.getProduct(productId);
        if (order != null && product != null) {
            Map<String,Integer> productList = order.getProductList();
            addProductToOrderHelper(quantity, order, productList, product);
        } else {
            throw new NotFoundException("order not found");
        }
    }





    // READ
    public List<Order> getOrderList() {
        return orderRepo.findAll();
    }

    public Order getOrder(String id) {
        return orderRepo.findById(id).orElse(null);
    }

    public Map<String,Integer> getProductsOrder(String orderId) {
        Order order = getOrder(orderId);
        if (order != null) {
            return order.getProductList();
        }
        return null;
    }


    // UPDATE
    private Update getOrderUpdate(Order order) { // this work has a helper logic to update Order
        return new Update()
                .set("id", order.getId())
                .set("userId", order.getUserId())
                .set("totalAmount", order.getTotalAmount())
                .set("productList", order.getProductList())
                .set("status", order.getStatus())
                .set("orderDate", order.getOrderDate());
    }

    public void updateOrder(Order order) {
        if (order.getId() != null) {
            Query query = new Query(Criteria.where("_id").is(order.getId()));
            mongoTemplate.updateFirst(query, getOrderUpdate(order), Order.class);
        }
    }


    // DELETE
    public void deleteOrder(String id) {
        if (orderRepo.existsById(id)) {
            orderRepo.deleteById(id);
        }
    }

    public void deleteOrders(List<String> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) return;
        orderRepo.deleteAllById(orderIds);
    }

    public void deleteAllOrders() {
        orderRepo.deleteAll();
    }

    public void deleteProductFromOrder(String productId, String orderId) {
        Order order = getOrder(orderId);
        if (order != null) {
            Map<String,Integer> productList = order.getProductList();
            Product product = productService.getProduct(productId);

            if (product != null && productList.containsKey(productId)) {
                productList.remove(productId);
                updateOrder(order);
            }
        }
    }

}
