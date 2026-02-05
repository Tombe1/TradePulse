package com.tradepulse.api.service;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.PortfolioItem;
import com.tradepulse.api.model.Transaction;
import com.tradepulse.api.model.User;
import com.tradepulse.api.repository.AssetRepository;
import com.tradepulse.api.repository.PortfolioItemRepository;
import com.tradepulse.api.repository.TransactionRepository;
import com.tradepulse.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PortfolioService {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PortfolioItemRepository itemRepository;
    private final TransactionRepository transactionRepository;

    public PortfolioService(UserRepository userRepository,
                            AssetRepository assetRepository,
                            PortfolioItemRepository itemRepository,
                            TransactionRepository transactionRepository) {
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

        // 1. שמירת היסטוריה
        if (quantity != 0) {
            Transaction.TransactionType type = quantity > 0 ? Transaction.TransactionType.BUY : Transaction.TransactionType.SELL;
            Transaction tx = new Transaction(user, symbol, type, Math.abs(quantity), price);
            transactionRepository.save(tx);
        }

        // שליפת הפריט הקיים או יצירת חדש
        PortfolioItem item = itemRepository.findByUserAndAsset(user, asset)
                .orElse(new PortfolioItem(user, asset, 0, BigDecimal.ZERO));

        // חישוב הכמות החדשה
        int currentQty = item.getQuantity();
        int newQuantity = currentQty + quantity;

        // --- התיקון הקריטי: מחיקה מוחלטת מהדאטה-בייס ומהזיכרון ---
        if (newQuantity <= 0) {
            if (item.getId() != null) {
                // 1. מנתקים את המניה מהרשימה של המשתמש בזיכרון (כדי ש-Hibernate לא יחזיר אותה)
                if (user.getPortfolioItems() != null) {
                    user.getPortfolioItems().remove(item);
                }

                // 2. מוחקים מהדאטה-בייס
                itemRepository.delete(item);

                // 3. מכריחים את השרת לבצע את המחיקה עכשיו (Flush)
                itemRepository.flush();
            }
            return null; // מחזירים null כי המניה נמחקה
        }

        // --- המשך לוגיקה רגילה (עדכון) ---

        // חישוב ממוצע מחיר (רק בקנייה משפיע על הממוצע)
        if (quantity > 0) {
            BigDecimal qtyAdded = BigDecimal.valueOf(quantity);
            BigDecimal pricePaid = BigDecimal.valueOf(price);
            BigDecimal currentQtyBD = BigDecimal.valueOf(currentQty);
            BigDecimal currentAvg = item.getAverageBuyPrice() != null ? item.getAverageBuyPrice() : BigDecimal.ZERO;

            BigDecimal totalCostOld = currentQtyBD.multiply(currentAvg);
            BigDecimal totalCostNew = qtyAdded.multiply(pricePaid);
            BigDecimal totalNewQty = currentQtyBD.add(qtyAdded);

            if (totalNewQty.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal newAverage = totalCostOld.add(totalCostNew).divide(totalNewQty, 2, RoundingMode.HALF_UP);
                item.setAverageBuyPrice(newAverage);
            }
        }

        // עדכון הכמות החדשה ושמירה
        item.setQuantity(newQuantity);
        return itemRepository.save(item);
    }

    @Transactional
    public PortfolioItem addToWatchlist(String username, String symbol) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Asset asset = assetRepository.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Asset not found"));

        return itemRepository.findByUserAndAsset(user, asset)
                .orElseGet(() -> itemRepository.save(new PortfolioItem(user, asset, 0, BigDecimal.ZERO)));
    }

    // פונקציית העזר לשליפת התיק הנקי (חשוב שתישאר)
    public List<PortfolioItem> getUserPortfolio(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // שליפה ישירה מהטבלה
        List<PortfolioItem> items = itemRepository.findByUser(user);

        // סינון נוסף ליתר ביטחון
        items.removeIf(item -> item.getQuantity() <= 0);

        return items;
    }
}