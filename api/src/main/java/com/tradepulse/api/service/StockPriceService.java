package com.tradepulse.api.service;

import com.tradepulse.api.model.QuoteResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
public class StockPriceService {

    private final RestClient restClient;
    private final String apiKey;

    // --- 1. DTO פנימי להחזקת שני המחירים ---
    @Data
    @AllArgsConstructor
    public static class StockQuote {
        private BigDecimal current;
        private BigDecimal previousClose;
    }

    public StockPriceService(@Value("${finnhub.api.url}") String baseUrl,
                             @Value("${finnhub.api.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }

    // --- 2. עדכון הפונקציה להחזרת האובייקט החדש ---
    // (ביטלתי את ה-Cache לרגע כי שומרים אובייקט מורכב, אם תרצה אפשר להחזיר)
    // @Cacheable(value = "stock_prices", key = "#symbol")
    public StockQuote fetchQuote(String symbol) {

        System.out.println("--- 🌍 Calling External API for " + symbol + " ---");

        // ביצוע בקשת GET ל-API החיצוני
        QuoteResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .body(QuoteResponse.class);

        if (response != null && response.getCurrentPrice() != null) {
            BigDecimal current = response.getCurrentPrice();

            // שליפת מחיר הסגירה הקודם (אם אין - נשתמש בנוכחי כברירת מחדל)
            BigDecimal prev = response.getPreviousClosePrice();
            if (prev == null) {
                prev = current;
            }

            return new StockQuote(current, prev);
        }

        throw new RuntimeException("Failed to fetch price for " + symbol);
    }
}