package com.tradepulse.api.repository;

import com.tradepulse.api.model.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {

    // שליפת כל ההיסטוריה של מניה מסוימת (נשתמש בזה לגרף)
    List<PriceHistory> findBySymbol(String symbol);

    // אופציה למתקדמים: שליפת 100 העדכונים האחרונים בלבד (כדי לא להעמיס)
    List<PriceHistory> findTop100BySymbolOrderByTimestampDesc(String symbol);
}