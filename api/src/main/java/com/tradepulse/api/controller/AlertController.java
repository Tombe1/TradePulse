package com.tradepulse.api.controller;

import com.tradepulse.api.model.Alert;
import com.tradepulse.api.model.AlertCondition;
import com.tradepulse.api.service.AlertService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // יצירת התראה חדשה
    // דוגמה לקריאה: POST /api/alerts?symbol=BTC&target=100000&condition=ABOVE
    @PostMapping
    public Alert createAlert(
            @RequestParam String symbol,
            @RequestParam double target,
            @RequestParam AlertCondition condition) {

        // שליפת המשתמש המחובר מהטוקן
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return alertService.createAlert(username, symbol, target, condition);
    }

    // קבלת כל ההתראות של המשתמש המחובר
    @GetMapping
    public List<Alert> getMyAlerts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return alertService.getUserAlerts(username);
    }
}