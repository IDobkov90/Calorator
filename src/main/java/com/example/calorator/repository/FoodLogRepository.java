package com.example.calorator.repository;

import com.example.calorator.model.entity.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {
}
