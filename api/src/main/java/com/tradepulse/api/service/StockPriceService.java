package com.tradepulse.api.service;

import com.tradepulse.api.model.QuoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Service
public class StockPriceService {

    private final RestClient restClient;
    private final String apiKey;

    // הזרקת הנתונים מקובץ ה-application.yml
    public StockPriceService(@Value("${finnhub.api.url}") String baseUrl,
                             @Value("${finnhub.api.key}") String apiKey) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.apiKey = apiKey;
    }

    @Cacheable(value = "stock_prices", key = "#symbol")
    public BigDecimal fetchPrice(String symbol) {

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
            return response.getCurrentPrice();
        }

        throw new RuntimeException("Failed to fetch price for " + symbol);
    }
}