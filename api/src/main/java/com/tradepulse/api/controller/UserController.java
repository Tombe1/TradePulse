package com.tradepulse.api.controller;

import com.tradepulse.api.model.PortfolioItem;
import com.tradepulse.api.model.Transaction;
import com.tradepulse.api.repository.TransactionRepository;
import com.tradepulse.api.repository.UserRepository;
import com.tradepulse.api.service.PortfolioService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PortfolioService portfolioService;
    private final TransactionRepository transactionRepository;

    public UserController(UserRepository userRepository,
                          PortfolioService portfolioService,
                          TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.portfolioService = portfolioService;
        this.transactionRepository = transactionRepository;
    }

    // ✅ התיקון הגדול: שינינו ל-List וקוראים ישירות מהסרביס
    // זה מה שיפתור את הבעיה שהמניות חוזרות אחרי מחיקה
    @GetMapping("/portfolio")
    public List<PortfolioItem> getMyPortfolio() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return portfolioService.getUserPortfolio(username);
    }

    @PostMapping("/trade")
    public PortfolioItem addTrade(
            @RequestParam String symbol,
            @RequestParam int qty,
            @RequestParam double price) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return portfolioService.addTransaction(username, symbol, qty, price);
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return transactionRepository.findByUserUsernameOrderByTimestampDesc(username);
    }
}