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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AlertService(AlertRepository alertRepository, UserRepository userRepository, SimpMessagingTemplate messagingTemplate) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Alert createAlert(String username, String symbol, double targetPrice, AlertCondition condition) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Alert alert = new Alert(user, symbol, BigDecimal.valueOf(targetPrice), condition);
        return alertRepository.save(alert);
    }

    @Transactional
    public void checkAlerts(Asset asset) {
        // שימוש בשם המותאם IsTriggered
        List<Alert> activeAlerts = alertRepository.findBySymbolAndIsTriggeredFalse(asset.getSymbol());

        for (Alert alert : activeAlerts) {
            boolean isHit = false;
            BigDecimal current = asset.getCurrentPrice();
            BigDecimal target = alert.getTargetPrice();

            if (alert.getCondition() == AlertCondition.ABOVE && current.compareTo(target) >= 0) {
                isHit = true;
            } else if (alert.getCondition() == AlertCondition.BELOW && current.compareTo(target) <= 0) {
                isHit = true;
            }

            if (isHit) {
                triggerAlert(alert, current);
            }
        }
    }

    private void triggerAlert(Alert alert, BigDecimal currentPrice) {
        alert.setTriggered(true); // Lombok יודע לייצר setTriggered גם לשדה isTriggered
        alert.setTriggeredAt(LocalDateTime.now()); // השדה שהוספנו למודל
        alertRepository.save(alert);

        String message = "🔔 התראה: " + alert.getSymbol() + " הגיע ליעד ($" + currentPrice + ")";

        Map<String, String> notification = new HashMap<>();
        notification.put("username", alert.getUser().getUsername());
        notification.put("message", message);

        messagingTemplate.convertAndSend("/topic/alerts", notification);
    }

    public List<Alert> getUserAlerts(String username) {
        return alertRepository.findByUserUsernameAndIsTriggeredFalse(username);
    }

    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }
}