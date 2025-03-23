package org.apiimplementation.apiimplementations.components;

import org.apiimplementation.apiimplementations.model.Product;
import org.apiimplementation.apiimplementations.model.User;
import org.apiimplementation.apiimplementations.service.OrderService;
import org.apiimplementation.apiimplementations.service.ProductService;
import org.apiimplementation.apiimplementations.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class DataLoader implements CommandLineRunner {

    private final ProductService productService;
    private final UserService userService;
    private final OrderService orderService;

    public DataLoader(ProductService productService, UserService userService, OrderService orderService) {
        this.productService = productService;
        this.userService = userService;
        this.orderService = orderService;
    }

    public static String generateValidId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 24);
    }

    @Override
    public void run(String... args) {

//        productService.deleteAllProduct();
//        userService.deleteAllUsers();
//        orderService.deleteAllOrders();
//
//        loadProducts();
//        loadUsersAndOrders();

        readData();
    }

    private void loadProducts() {
        List<Product> products = Arrays.asList(

                new Product("USB Flash", 20.00,10),
                new Product("Keyboard", 45.00,2),
                new Product( "Mouse", 25.00,5),
                new Product( "External HDD", 120.00,12),
                new Product( "Monitor", 250.00,4)
        );

        products.forEach(productService::addProduct);
        System.out.println("Products added successfully!");
    }

    private void loadUsersAndOrders() {
        List<User> users = Arrays.asList(
                new User("Prince", "Matongo", "prince@example.com",
                        LocalDate.of(2004, 1, 1)),
                new User( "Merino", "R", "merino@example.com", LocalDate.of(1998, 7, 12)),
                new User( "Daniele", "B", "daniele@example.com", LocalDate.of(1995, 3, 22)),
                new User( "Laura", "V", "laura@example.com", LocalDate.of(1999, 6, 30)),
                new User( "Marco", "N", "marco@example.com", LocalDate.of(1992, 11, 8))
        );

        users.forEach(userService::addUser);
        System.out.println("Users added successfully!");
    }


    public void readData() {
        System.out.println("User List\n-------------");
        for (User user : userService.getUserList()) {
            System.out.println(user.toString());
        }
        System.out.println("---------------------------------");
        System.out.println("Product List\n-------------");
        for (Product product : productService.getProductList()) {
            System.out.println(product.toString());
        }
    }

}
