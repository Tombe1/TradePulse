package com.tradepulse.api.repository;

import com.tradepulse.api.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    // שינינו ל-IsTriggered כדי שיתאים לשדה isTriggered במודל שלך
    List<Alert> findBySymbolAndIsTriggeredFalse(String symbol);

    List<Alert> findByUserUsernameAndIsTriggeredFalse(String username);
}