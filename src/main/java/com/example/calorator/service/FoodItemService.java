package com.example.calorator.service;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;

import java.util.List;

public interface FoodItemService {
    List<FoodItemDTO> getAllFoodItems();

    FoodItemDTO getFoodItemById(Long id);

    List<FoodItemDTO> getFoodItemsByCategory(FoodCategory category);

    FoodItemDTO createFoodItem(FoodItemDTO foodItemDTO);

    FoodItemDTO updateFoodItem(FoodItemDTO foodItemDTO);

    void deleteFoodItem(Long id);
}
