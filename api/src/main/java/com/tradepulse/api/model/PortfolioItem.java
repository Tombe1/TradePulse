package com.tradepulse.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // השוואה רק לפי ID
@Table(name = "portfolio_items")
public class PortfolioItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude // מונע לולאה
    private User user;

    @ManyToOne
    @JoinColumn(name = "asset_symbol", nullable = false)
    private Asset asset;

    private int quantity = 0;

    private BigDecimal averageBuyPrice = BigDecimal.ZERO;

    public PortfolioItem(User user, Asset asset, int quantity, BigDecimal averageBuyPrice) {
        this.user = user;
        this.asset = asset;
        this.quantity = quantity;
        this.averageBuyPrice = averageBuyPrice;
    }
}