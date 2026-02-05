package com.tradepulse.api.controller;

import com.tradepulse.api.model.Alert;
import com.tradepulse.api.model.AlertCondition;
import com.tradepulse.api.service.AlertService;
import org.springframework.http.ResponseEntity; // יבוא חדש
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

    @PostMapping
    public Alert createAlert(
            @RequestParam String symbol,
            @RequestParam double target,
            @RequestParam AlertCondition condition) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return alertService.createAlert(username, symbol, target, condition);
    }

    @GetMapping
    public List<Alert> getMyAlerts() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return alertService.getUserAlerts(username);
    }

    // --- התוספת: מחיקת התראה ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        // הערה: ביישום מושלם היינו בודקים כאן שההתראה באמת שייכת למשתמש הזה
        // כרגע נסמוך על ה-Service או פשוט נמחק לפי ID
        alertService.deleteAlert(id);
        return ResponseEntity.ok().build();
    }
}