package com.tradepulse.api.service;

import com.tradepulse.api.model.*;
import com.tradepulse.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PortfolioService {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PortfolioItemRepository itemRepository;
    private final TransactionRepository transactionRepository; // <--- 1. חדש

    // בנאי מעודכן
    public PortfolioService(UserRepository userRepository,
                            AssetRepository assetRepository,
                            PortfolioItemRepository itemRepository,
                            TransactionRepository transactionRepository) { // <--- 2. חדש
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.itemRepository = itemRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public PortfolioItem addTransaction(String username, String symbol, int quantity, double price) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Asset asset = assetRepository.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        // --- התיקון: שומרים היסטוריה רק אם בוצעה פעולה אמיתית (קנייה/מכירה) ---
        if (quantity != 0) {
            Transaction.TransactionType type = quantity > 0 ? Transaction.TransactionType.BUY : Transaction.TransactionType.SELL;
            Transaction tx = new Transaction(user, symbol, type, Math.abs(quantity), price);
            transactionRepository.save(tx);
        }
        // --------------------------------------------------------------------

        PortfolioItem item = itemRepository.findByUserAndAsset(user, asset)
                .orElse(new PortfolioItem(user, asset, 0, BigDecimal.ZERO));

        // לוגיקת הממוצע (נשארת ללא שינוי)
        if (quantity > 0) {
            BigDecimal newQty = BigDecimal.valueOf(quantity);
            BigDecimal newPrice = BigDecimal.valueOf(price);
            BigDecimal currentQty = BigDecimal.valueOf(item.getQuantity());
            BigDecimal currentAvg = item.getAverageBuyPrice();

            BigDecimal totalCostOld = currentQty.multiply(currentAvg);
            BigDecimal totalCostNew = newQty.multiply(newPrice);
            BigDecimal totalQty = currentQty.add(newQty);

            // מניעת חלוקה ב-0 במקרה נדיר
            if (totalQty.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal newAverage = totalCostOld.add(totalCostNew).divide(totalQty, 2, RoundingMode.HALF_UP);
                item.setAverageBuyPrice(newAverage);
            }

            item.setQuantity(totalQty.intValue());
        }

        return itemRepository.save(item);
    }

    @Transactional
    public PortfolioItem addToWatchlist(String username, String symbol) {
        return addTransaction(username, symbol, 0, 0);
        // שים לב: זה יירשם כטרנזקציה עם כמות 0, אולי נרצה לסנן את זה אח"כ, אבל לבינתיים זה בסדר.
    }
}