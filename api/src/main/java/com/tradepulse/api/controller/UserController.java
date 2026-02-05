package com.tradepulse.api.controller;

import com.tradepulse.api.model.PortfolioItem;
import com.tradepulse.api.model.Transaction;
import com.tradepulse.api.model.User;
import com.tradepulse.api.repository.TransactionRepository;
import com.tradepulse.api.repository.UserRepository;
import com.tradepulse.api.service.PortfolioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PortfolioService portfolioService;
    private final TransactionRepository transactionRepository; // <--- 1. שדה חדש

    // 2. עדכון הבנאי (הוספת TransactionRepository)
    public UserController(UserRepository userRepository,
                          PortfolioService portfolioService,
                          TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.portfolioService = portfolioService;
        this.transactionRepository = transactionRepository;
    }

    // שליפת הפורטפוליו
    @GetMapping("/portfolio")
    public Set<PortfolioItem> getMyPortfolio() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPortfolioItems();
    }

    // הוספת קנייה (Trade)
    @PostMapping("/trade")
    public PortfolioItem addTrade(
            @RequestParam String symbol,
            @RequestParam int qty,
            @RequestParam double price) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return portfolioService.addTransaction(username, symbol, qty, price);
    }

    // הוספה למעקב (Watchlist)
    @PostMapping("/watchlist/{symbol}")
    public PortfolioItem addToWatchlist(@PathVariable String symbol) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return portfolioService.addToWatchlist(username, symbol);
    }

    // --- 3. Endpoint חדש להיסטוריה ---
    @GetMapping("/history")
    public List<Transaction> getHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return transactionRepository.findByUserUsernameOrderByTimestampDesc(username);
    }
}