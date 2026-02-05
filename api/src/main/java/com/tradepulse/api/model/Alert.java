package com.tradepulse.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private User user;

    private String symbol;

    private BigDecimal targetPrice;

    @Enumerated(EnumType.STRING)
    private AlertCondition condition;

    // השדה שלך נשאר כמו שהוא
    private boolean isTriggered = false;

    // --- התוספת היחידה שחייבים: זמן ההפעלה ---
    private LocalDateTime triggeredAt;
    // ------------------------------------------

    private LocalDateTime createdAt = LocalDateTime.now();

    public Alert(User user, String symbol, BigDecimal targetPrice, AlertCondition condition) {
        this.user = user;
        this.symbol = symbol;
        this.targetPrice = targetPrice;
        this.condition = condition;
    }
}