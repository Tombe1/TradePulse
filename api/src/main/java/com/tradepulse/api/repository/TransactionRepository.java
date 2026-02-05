package com.tradepulse.api.repository;

import com.tradepulse.api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // שליפת כל ההיסטוריה של משתמש מסוים, ממוין מהחדש לישן
    List<Transaction> findByUserUsernameOrderByTimestampDesc(String username);
}