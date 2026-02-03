package com.tradepulse.api.repository;

import com.tradepulse.api.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssetRepository extends JpaRepository<Asset, String> {
    // ריק!
    // עצם הירושה מ-JpaRepository נותנת לנו אוטומטית מתודות כמו:
    // save, findById, findAll, delete
    // בלי שנצטרך לכתוב שורת קוד אחת של מימוש.
}