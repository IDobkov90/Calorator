package com.example.calorator.repository;

import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByCategory(FoodCategory category);

    Page<FoodItem> findByCategory(FoodCategory category, Pageable pageable);

    List<FoodItem> findByNameContainingIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
}
