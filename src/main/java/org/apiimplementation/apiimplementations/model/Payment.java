package org.apiimplementation.apiimplementations.model;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apiimplementation.apiimplementations.model.status.PaymentStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;


@Document(collection = "Payments")

public class Payment {

    @Id
    private String id;

    private String orderId;

    private String userId;

    @Min(0)
    private double amount;

    private PaymentStatus paymentStatus;

    private LocalDateTime paymentDate = LocalDateTime.now();

    public Payment() {
    }

    public Payment(String orderId, String userId, double amount,
                   PaymentStatus paymentStatus, LocalDateTime paymentDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentDate = paymentDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }
}

