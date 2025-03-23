package org.apiimplementation.apiimplementations.model;

import jakarta.validation.constraints.Min;
import org.apiimplementation.apiimplementations.model.status.Status;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@Document(collection = "Orders")
public class Order {

    @Id
    private String id;
    private String userId;
    @Min(0)
    private Double totalAmount;
    private List<Product> productList;
    private Status status;
    private LocalDate orderDate;


    public Order() {
    }


    public Order(String userId, List<Product> productList) {
        this.userId = userId;
        this.totalAmount = totalAmount();
        this.productList = productList;
        this.status = Status.PENDING;
        this.orderDate = LocalDate.now();
    }




    public Double totalAmount(){
      List<Double> prices = getProductList().stream().map(Product::getPrice).toList();
      double total = 0.0;
      for(double price:prices){
          total += price;
      }
      return  total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Product> getProductList() {
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", totalAmount=" + totalAmount +
                ", productList=" + productList +
                ", status=" + status +
                ", orderDate=" + orderDate +
                '}';
    }
}

