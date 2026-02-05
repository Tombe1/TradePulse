package com.tradepulse.api.repository;

import com.tradepulse.api.model.Asset;
import com.tradepulse.api.model.PortfolioItem;
import com.tradepulse.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {
    // מציאת פריט ספציפי של משתמש ספציפי למניה ספציפית
    Optional<PortfolioItem> findByUserAndAsset(User user, Asset asset);
}