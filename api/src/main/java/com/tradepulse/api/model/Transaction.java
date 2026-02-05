package com.tradepulse.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    private String symbol;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // BUY or SELL

    private int quantity;
    private double price;

    private LocalDateTime timestamp = LocalDateTime.now();

    public enum TransactionType {
        BUY, SELL
    }

    public Transaction(User user, String symbol, TransactionType type, int quantity, double price) {
        this.user = user;
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }
}