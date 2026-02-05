package com.tradepulse.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuoteResponse {

    @JsonProperty("c")
    private BigDecimal currentPrice;

    // --- הוסף את השדה הזה: ---
    @JsonProperty("pc")
    private BigDecimal previousClosePrice;

    // שאר השדות (h, l, o...) יכולים להישאר אם יש לך אותם
}