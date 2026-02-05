package com.tradepulse.api.service;

import com.tradepulse.api.model.Alert;
import com.tradepulse.api.model.AlertCondition;
import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.User;
import com.tradepulse.api.repository.AlertRepository;
import com.tradepulse.api.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate; // הכלי לשליחת הודעות WS

    public AlertService(AlertRepository alertRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    // 1. יצירת התראה חדשה
    public Alert createAlert(String username, String symbol, double targetPrice, AlertCondition condition) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Alert alert = new Alert(user, symbol, BigDecimal.valueOf(targetPrice), condition);
        return alertRepository.save(alert);
    }

    // 2. בדיקת התראות (הפונקציה שתרוץ בכל עדכון מחיר)
    @Transactional
    public void checkAlerts(Asset asset) {
        // שולפים רק התראות פעילות למניה הספציפית הזו
        List<Alert> activeAlerts = alertRepository.findBySymbolAndIsTriggeredFalse(asset.getSymbol());

        for (Alert alert : activeAlerts) {
            boolean isHit = false;

            // בדיקה: האם עברנו את המחיר?
            if (alert.getCondition() == AlertCondition.ABOVE) {
                // אם המחיר הנוכחי >= מחיר היעד
                if (asset.getCurrentPrice().compareTo(alert.getTargetPrice()) >= 0) {
                    isHit = true;
                }
            } else if (alert.getCondition() == AlertCondition.BELOW) {
                // אם המחיר הנוכחי <= מחיר היעד
                if (asset.getCurrentPrice().compareTo(alert.getTargetPrice()) <= 0) {
                    isHit = true;
                }
            }

            if (isHit) {
                triggerAlert(alert, asset.getCurrentPrice());
            }
        }
    }

    // 3. ביצוע ההתראה בפועל
    private void triggerAlert(Alert alert, BigDecimal currentPrice) {
        alert.setTriggered(true);
        alertRepository.save(alert);

        String message = "🔔 התראה: " + alert.getSymbol() + " הגיע ליעד ($" + currentPrice + ")";

        System.out.println("Triggering alert for: " + alert.getUser().getUsername());

        // יצירת אובייקט הודעה עם הנמען
        Map<String, String> notification = new HashMap<>();
        notification.put("username", alert.getUser().getUsername());
        notification.put("message", message);

        // שליחה לערוץ הציבורי (הלקוח יסנן לבד)
        messagingTemplate.convertAndSend("/topic/alerts", notification);
    }

    // שליפת התראות של משתמש (להצגה ב-UI)
    public List<Alert> getUserAlerts(String username) {
        return alertRepository.findByUserUsername(username);
    }
}