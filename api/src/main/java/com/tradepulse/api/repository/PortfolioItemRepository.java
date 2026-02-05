package com.tradepulse.api.repository;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.PortfolioItem;
import com.tradepulse.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    Optional<PortfolioItem> findByUserAndAsset(User user, Asset asset);

    // --- השורה החדשה: שליפת רשימה עדכנית מהדאטהבייס ---
    List<PortfolioItem> findByUser(User user);
}