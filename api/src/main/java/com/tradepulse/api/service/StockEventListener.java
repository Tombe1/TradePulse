package com.tradepulse.api.service;

import com.tradepulse.api.model.Asset;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StockEventListener {

    private final AlertService alertService;

    // כאן אנחנו מזריקים את שירות ההתראות
    public StockEventListener(AlertService alertService) {
        this.alertService = alertService;
    }

    // --- המוח של קפקא ---
    // הפונקציה הזו "ישנה" עד שמגיעה הודעה ל-Topic "stock-updates"
    // groupId חשוב כדי שקפקא ידע מי הצרכן
    @KafkaListener(topics = "stock-updates", groupId = "tradepulse-group")
    public void handleStockUpdate(Asset asset) {
        System.out.println("<<< 📬 Kafka received update for: " + asset.getSymbol());

        // כאן מתבצעת בדיקת ההתראות - בנפרד לגמרי מה-Fetcher!
        alertService.checkAlerts(asset);
    }
}