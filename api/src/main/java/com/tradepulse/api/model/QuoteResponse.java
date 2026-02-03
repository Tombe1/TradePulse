package com.tradepulse.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class QuoteResponse {
    // אנחנו ממפים את השדה "c" מה-JSON למשתנה ברור יותר
    @JsonProperty("c")
    private BigDecimal currentPrice;
}