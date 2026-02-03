package com.tradepulse.api.controller;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.User;
import com.tradepulse.api.repository.AssetRepository;
import com.tradepulse.api.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final AssetRepository assetRepository;

    public UserController(UserRepository userRepository, AssetRepository assetRepository) {
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
    }

    // 1. יצירת משתמש חדש
    // POST /api/users
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userRepository.save(user);
    }

    // 2. הוספת מניה לתיק של משתמש
    // POST /api/users/{username}/portfolio/{symbol}
    @PostMapping("/{username}/portfolio/{symbol}")
    public User addAssetToPortfolio(@PathVariable String username, @PathVariable String symbol) {
        // חיפוש המשתמש
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        // חיפוש המניה (חייבת להיות קיימת במערכת קודם!)
        Asset asset = assetRepository.findById(symbol)
                .orElseThrow(() -> new RuntimeException("Asset not found: " + symbol));

        // הוספה לתיק ושמירה
        user.getPortfolio().add(asset);
        return userRepository.save(user);
    }

    // 3. קבלת התיק האישי
    // GET /api/users/{username}/portfolio
    @GetMapping("/{username}/portfolio")
    public Set<Asset> getUserPortfolio(@PathVariable String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getPortfolio();
    }
}