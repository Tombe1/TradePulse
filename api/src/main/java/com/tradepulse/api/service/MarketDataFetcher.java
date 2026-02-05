package com.tradepulse.api.service;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.PriceHistory;
import com.tradepulse.api.repository.AssetRepository;
import com.tradepulse.api.repository.PriceHistoryRepository;
import org.springframework.kafka.core.KafkaTemplate; // <--- יבוא חדש לקפקא
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketDataFetcher {

    private final AssetRepository assetRepository;
    private final PriceHistoryRepository historyRepository;
    private final StockPriceService stockService;
    private final SimpMessagingTemplate messagingTemplate;

    // שינוי 1: במקום AlertService, אנחנו מזריקים את KafkaTemplate
    // <String, Asset> אומר: המפתח הוא טקסט, וההודעה היא אובייקט של מניה
    private final KafkaTemplate<String, Asset> kafkaTemplate;

    public MarketDataFetcher(AssetRepository assetRepository,
                             PriceHistoryRepository historyRepository,
                             StockPriceService stockService,
                             SimpMessagingTemplate messagingTemplate,
                             KafkaTemplate<String, Asset> kafkaTemplate) { // <--- הזרקה בבנאי
        this.assetRepository = assetRepository;
        this.historyRepository = historyRepository;
        this.stockService = stockService;
        this.messagingTemplate = messagingTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedRate = 30000)
    public void updateMarketData() {
        List<Asset> allAssets = assetRepository.findAll();
        if (allAssets.isEmpty()) return;

        for (Asset existingAsset : allAssets) {
            updateAsset(existingAsset);
        }
    }

    private void updateAsset(Asset asset) {
        try {
            StockPriceService.StockQuote quote = stockService.fetchQuote(asset.getSymbol());
            LocalDateTime now = LocalDateTime.now();

            asset.setCurrentPrice(quote.getCurrent());
            asset.setPreviousClosePrice(quote.getPreviousClose());
            asset.setLastUpdated(now);
            assetRepository.save(asset);

            PriceHistory history = new PriceHistory(asset.getSymbol(), quote.getCurrent(), now);
            historyRepository.save(history);

            // עדכון ל-Frontend (נשאר אותו דבר)
            System.out.println(">>> Broadcasting update for " + asset.getSymbol());
            messagingTemplate.convertAndSend("/topic/prices", asset);

            // --- שינוי 2: שליחה לקפקא ---
            // במקום לבדוק התראות בעצמנו, אנחנו רק מודיעים: "המחיר התעדכן!"
            // ושולחים את זה לצינור שנקרא "stock-updates"
            System.out.println(">>> 📨 Sending Kafka event for " + asset.getSymbol());
            kafkaTemplate.send("stock-updates", asset);

        } catch (Exception e) {
            System.err.println("Error updating " + asset.getSymbol() + ": " + e.getMessage());
        }
    }
}