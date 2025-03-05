package com.example.calorator.service;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FoodItemService {
    Page<FoodItemDTO> getAllFoodItems(Pageable pageable);

    List<FoodItemDTO> getAllFoodItems();

    FoodItemDTO getFoodItemById(Long id);

    List<FoodItemDTO> getFoodItemsByCategory(FoodCategory category);

    Page<FoodItemDTO> getFoodItemsByCategory(FoodCategory category, Pageable pageable);

    FoodItemDTO createFoodItem(FoodItemDTO foodItemDTO);

    FoodItemDTO updateFoodItem(FoodItemDTO foodItemDTO);

    void deleteFoodItem(Long id);
}
