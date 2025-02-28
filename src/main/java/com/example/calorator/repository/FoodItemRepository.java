package com.example.calorator.repository;

import com.example.calorator.model.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> { // Define the interface with CRUD operations{
}
