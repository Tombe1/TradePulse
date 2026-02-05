package com.tradepulse.api.repository;

import com.tradepulse.api.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    // שליפת כל ההתראות הפעילות למניה מסוימת (למשל: כל מי שמחכה ל-PLTR)
    List<Alert> findBySymbolAndIsTriggeredFalse(String symbol);

    // שליפת כל ההתראות של משתמש מסוים (כדי להציג לו רשימה באתר)
    List<Alert> findByUserUsername(String username);
}