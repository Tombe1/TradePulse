package com.tradepulse.api.config;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.repository.AssetRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final AssetRepository assetRepository;

    public DataLoader(AssetRepository assetRepository) {
        this.assetRepository = assetRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // בדיקה: האם הטבלה כבר מלאה? אם כן, לא עושים כלום.
        if (assetRepository.count() > 0) {
            return;
        }

        System.out.println("🌱 Seeding database with initial assets...");

        // רשימת המניות והקריפטו לטעינה ראשונית
        List<Asset> initialAssets = List.of(
                // --- טכנולוגיה ומניות חמות ---
                createAsset("PLTR", "Palantir Technologies"),
                createAsset("TSLA", "Tesla Inc"),
                createAsset("AAPL", "Apple Inc"),
                createAsset("NVDA", "NVIDIA Corp"),
                createAsset("AMZN", "Amazon.com Inc"),
                createAsset("MSFT", "Microsoft Corp"),
                createAsset("GOOGL", "Alphabet Inc"),
                createAsset("AMD", "Advanced Micro Devices"),

                // --- פינטק ובנקים ---
                createAsset("SOFI", "SoFi Technologies"),
                createAsset("PYPL", "PayPal Holdings"),
                createAsset("UPST", "Upstart Holdings"),
                createAsset("PGY", "Pagaya Technologies"),

                // --- קריפטו (דרך Coinbase כפי שסידרנו) ---
                createAsset("COINBASE:BTC-USD", "Bitcoin"),
                createAsset("COINBASE:ETH-USD", "Ethereum"),
                createAsset("COINBASE:SOL-USD", "Solana"),

                // --- כריית קריפטו ---
                createAsset("MARA", "Marathon Digital"),
                createAsset("HUT", "Hut 8 Corp"),
                createAsset("COIN", "Coinbase Global")
        );

        assetRepository.saveAll(initialAssets);
        System.out.println("✅ Database seeded with " + initialAssets.size() + " assets!");
    }

    private Asset createAsset(String symbol, String name) {
        Asset asset = new Asset();
        asset.setSymbol(symbol);
        asset.setCompanyName(name);
        asset.setCurrentPrice(BigDecimal.ZERO); // יתעדכן אוטומטית ע"י ה-Fetcher בהמשך
        asset.setLastUpdated(LocalDateTime.now());
        return asset;
    }
}