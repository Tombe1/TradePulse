package com.tradepulse.api.service;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.PriceHistory;
import com.tradepulse.api.repository.AssetRepository;
import com.tradepulse.api.repository.PriceHistoryRepository;
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
    private final AlertService alertService;

    public MarketDataFetcher(AssetRepository assetRepository,
                             PriceHistoryRepository historyRepository,
                             StockPriceService stockService,
                             SimpMessagingTemplate messagingTemplate,
                             AlertService alertService) {
        this.assetRepository = assetRepository;
        this.historyRepository = historyRepository;
        this.stockService = stockService;
        this.messagingTemplate = messagingTemplate;
        this.alertService = alertService;
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
            // --- שינוי 1: שימוש ב-fetchQuote שמחזיר אובייקט עם שני מחירים ---
            StockPriceService.StockQuote quote = stockService.fetchQuote(asset.getSymbol());
            LocalDateTime now = LocalDateTime.now();

            // --- שינוי 2: עדכון גם של המחיר הנוכחי וגם של הסגירה הקודמת ---
            asset.setCurrentPrice(quote.getCurrent());
            asset.setPreviousClosePrice(quote.getPreviousClose()); // <--- הנתון החדש לחישוב P&L יומי!
            asset.setLastUpdated(now);
            assetRepository.save(asset);

            // 2. שמירת היסטוריה (משתמשים במחיר הנוכחי)
            PriceHistory history = new PriceHistory(asset.getSymbol(), quote.getCurrent(), now);
            historyRepository.save(history);

            // 3. שידור ל-WebSocket
            System.out.println(">>> Broadcasting update for " + asset.getSymbol());
            messagingTemplate.convertAndSend("/topic/prices", asset);

            // 4. בדיקת התראות
            alertService.checkAlerts(asset);

        } catch (Exception e) {
            System.err.println("Error updating " + asset.getSymbol() + ": " + e.getMessage());
        }
    }
}