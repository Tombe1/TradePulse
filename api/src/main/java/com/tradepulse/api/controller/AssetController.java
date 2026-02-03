package com.tradepulse.api.controller;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.repository.AssetRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // מגדיר שזהו רכיב שמחזיר תשובות JSON
@RequestMapping("/api/assets") // כתובת הבסיס לכל הבקשות כאן
public class AssetController {

    private final AssetRepository repository;

    // הזרקת תלות (Dependency Injection) דרך הבנאי
    public AssetController(AssetRepository repository) {
        this.repository = repository;
    }

    // פעולה 1: שליפת כל המניות
    // GET http://localhost:8080/api/assets
    @GetMapping
    public List<Asset> getAllAssets() {
        return repository.findAll();
    }

    // פעולה 2: הוספת מניה חדשה (נשתמש בזה עוד מעט)
    // POST http://localhost:8080/api/assets
    @PostMapping
    public Asset createAsset(@RequestBody Asset asset) {
        return repository.save(asset);
    }
}