package com.tradepulse.api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asset {

    @Id
    @Column(unique = true, length = 50)
    private String symbol;

    @Column(nullable = false)
    private String companyName;

    @Column(precision = 10, scale = 2)
    private BigDecimal currentPrice;

    // --- התיקון: הוספת הגדרת עמודה כדי לשמור על דיוק כספי ---
    @Column(precision = 10, scale = 2)
    private BigDecimal previousClosePrice = BigDecimal.ZERO;

    private LocalDateTime lastUpdated;
}