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
import java.util.List;

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
        orderRepo.save(order);
        System.out.println("Phase 6");
    }


    public void addProductToNewOrder(String productId, int quantity, String userId) {
        System.out.println("Phase 2");
        System.out.println(productId);
        System.out.println(userId);

        Product product = productService.getProduct(productId);
        if(userService.getUser(userId).isPresent() && product != null){
            System.out.println("Phase 3");
            Order order = new Order();

            order.setProductList(new ArrayList<>());
            order.setUserId(userId);
            order.setStatus(Status.PENDING);
            order.setOrderDate(LocalDate.now());
            order.setId(DataLoader.generateValidId());

            List<Product> productList = order.getProductList();
            addProductToOrderHelper(quantity, order, productList, product);
        }else{
            System.out.println("Phase 3 fails");
            throw new NotFoundException("User or Product not found");
        }
    }


    private void addProductToOrderHelper(int quantity, Order order, List<Product> productList, Product product) {
        if (product != null && product.getStockQuantity() - quantity > -1) {

            // add product to order product list
            product.setQuantity(quantity);
            productList.add(product);
            order.setProductList(productList);


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
            List<Product> productList = order.getProductList();
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

    public List<Product> getProductsOrder(String orderId) throws Exception {
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
            List<Product> productList = order.getProductList();
            Product product = productService.getProduct(productId);
            if (product != null) {

                productList.remove(product);
            }
        }
    }

}
