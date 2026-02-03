package com.tradepulse.api.controller;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.PriceHistory;
import com.tradepulse.api.repository.AssetRepository;
import com.tradepulse.api.repository.PriceHistoryRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetRepository assetRepository;
    private final PriceHistoryRepository historyRepository; // <-- תוספת

    // עדכון הבנאי לקבלת ה-Repository החדש
    public AssetController(AssetRepository assetRepository, PriceHistoryRepository historyRepository) {
        this.assetRepository = assetRepository;
        this.historyRepository = historyRepository;
    }

    @GetMapping
    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return assetRepository.save(asset);
    }

    // --- הפונקציה החדשה לגרף ---
    // GET http://localhost:8080/api/assets/PLTR/history
    @GetMapping("/{symbol}/history")
    public List<PriceHistory> getAssetHistory(@PathVariable String symbol) {
        return historyRepository.findBySymbol(symbol);
    }
}