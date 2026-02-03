package com.tradepulse.api.service;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.repository.AssetRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class MarketDataFetcher {

    private final AssetRepository repository;
    private final StockPriceService stockService;
    private final SimpMessagingTemplate messagingTemplate;

    public MarketDataFetcher(AssetRepository repository,
                             StockPriceService stockService,
                             SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.stockService = stockService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 10000) // רץ כל 10 שניות
    public void updateMarketData() {
        // 1. שליפת כל המניות ששמורות במערכת
        List<Asset> allAssets = repository.findAll();

        if (allAssets.isEmpty()) {
            System.out.println("--- No assets to track ---");
            return;
        }

        // 2. מעבר בלולאה ועדכון כל אחת מהן
        for (Asset existingAsset : allAssets) {
            updateAsset(existingAsset);
        }
    }

    private void updateAsset(Asset asset) {
        try {
            // שליפת מחיר עדכני (דרך הקאש!)
            BigDecimal newPrice = stockService.fetchPrice(asset.getSymbol());

            // עדכון האובייקט
            asset.setCurrentPrice(newPrice);
            asset.setLastUpdated(java.time.LocalDateTime.now());

            // שמירה ב-DB
            repository.save(asset);

            // שידור ל-WebSocket
            System.out.println(">>> Broadcasting update for " + asset.getSymbol());
            messagingTemplate.convertAndSend("/topic/prices", asset);

        } catch (Exception e) {
            System.err.println("Error updating " + asset.getSymbol() + ": " + e.getMessage());
        }
    }
}