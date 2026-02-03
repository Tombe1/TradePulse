package com.tradepulse.api.service;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.PriceHistory; // <-- יבוא חדש
import com.tradepulse.api.repository.AssetRepository;
import com.tradepulse.api.repository.PriceHistoryRepository; // <-- יבוא חדש
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MarketDataFetcher {

    private final AssetRepository assetRepository;
    private final PriceHistoryRepository historyRepository; // <-- שדה חדש
    private final StockPriceService stockService;
    private final SimpMessagingTemplate messagingTemplate;

    // עדכון הבנאי
    public MarketDataFetcher(AssetRepository assetRepository,
                             PriceHistoryRepository historyRepository, // <-- פרמטר חדש
                             StockPriceService stockService,
                             SimpMessagingTemplate messagingTemplate) {
        this.assetRepository = assetRepository;
        this.historyRepository = historyRepository;
        this.stockService = stockService;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 10000)
    public void updateMarketData() {
        List<Asset> allAssets = assetRepository.findAll();
        if (allAssets.isEmpty()) return;

        for (Asset existingAsset : allAssets) {
            updateAsset(existingAsset);
        }
    }

    private void updateAsset(Asset asset) {
        try {
            BigDecimal newPrice = stockService.fetchPrice(asset.getSymbol());
            LocalDateTime now = LocalDateTime.now();

            // 1. עדכון המניה הנוכחית (כמו קודם)
            asset.setCurrentPrice(newPrice);
            asset.setLastUpdated(now);
            assetRepository.save(asset);

            // 2. --- החדש: שמירת היסטוריה ---
            PriceHistory history = new PriceHistory(asset.getSymbol(), newPrice, now);
            historyRepository.save(history);
            // -----------------------------

            System.out.println(">>> Broadcasting update for " + asset.getSymbol());
            messagingTemplate.convertAndSend("/topic/prices", asset);

        } catch (Exception e) {
            System.err.println("Error updating " + asset.getSymbol() + ": " + e.getMessage());
        }
    }
}