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

@Entity // אומר לספרינג: "זו טבלה בבסיס הנתונים"
@Table(name = "assets") // השם של הטבלה ב-DB יהיה assets
@Data // לומבוק: יוצר אוטומטית Getters, Setters, toString
@NoArgsConstructor // לומבוק: יוצר בנאי ריק (חובה ל-JPA)
@AllArgsConstructor // לומבוק: יוצר בנאי עם כל השדות
public class Asset {

    @Id // המפתח הראשי (Primary Key)
    @Column(unique = true, length = 50)
    private String symbol; // למשל: "AAPL", "PLTR" - הסימול הוא ייחודי

    @Column(nullable = false)
    private String companyName;

    // בפיננסים תמיד משתמשים ב-BigDecimal ולא ב-Double כדי למנוע טעויות עיגול!
    @Column(precision = 10, scale = 2)
    private BigDecimal currentPrice;

    private LocalDateTime lastUpdated;
}