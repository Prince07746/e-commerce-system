package org.apiimplementation.apiimplementations.service;


import org.apiimplementation.apiimplementations.repository.ProductRepository;
import org.apiimplementation.apiimplementations.model.Product;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepo;
    private final MongoTemplate mongoTemplate;

    public ProductService(ProductRepository productRepo, MongoTemplate mongoTemplate) {
        this.productRepo = productRepo;
        this.mongoTemplate = mongoTemplate;
    }



    // CREATE
    public void addProduct(Product product) {
        if (product == null || product.getName() == null || product.getPrice() == null) {
            throw new IllegalArgumentException("Product details are incomplete.");
        }
        productRepo.save(product);
    }


    // READ
    public List<Product> getProductList() {
        return productRepo.findAll();
    }

    public Product getProduct(String id) {
        return productRepo.findById(id).orElse(null);
    }


    // UPDATE

    private Update getProductUpdate(Product product){ // this work has a helper logic to update Product
        return new Update()
                .set("id",product.getId())
                .set("name", product.getName())
                .set("price", product.getPrice())
                .set("stockQuantity",product.getStockQuantity());
    }

    public void updateProduct(Product product) {
        if (product.getId() != null) {
            Query query = new Query(Criteria.where("_id").is(product.getId()));
            mongoTemplate.updateFirst(query, getProductUpdate(product), Product.class);
        }
    }




    // DELETE
    public void deleteProduct(String id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
        }
    }

    public void deleteProducts(List<String> productIds) {
        if (productIds == null || productIds.isEmpty()) return;
        productRepo.deleteAllById(productIds);
    }

    public void deleteAllProduct(){
        productRepo.deleteAll();
    }
}
