package com.example.calorator.service;

import com.example.calorator.model.dto.FoodLogDTO;
import com.example.calorator.model.dto.NutritionSummaryDTO;
import com.example.calorator.model.enums.MealType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FoodLogService {

    FoodLogDTO createFoodLog(FoodLogDTO foodLogDTO, String username);
    
    FoodLogDTO updateFoodLog(Long id, FoodLogDTO foodLogDTO, String username);
    
    void deleteFoodLog(Long id, String username);
    
    FoodLogDTO getFoodLogById(Long id, String username);
    
    List<FoodLogDTO> getFoodLogsByDate(LocalDate date, String username);
    
    List<FoodLogDTO> getFoodLogsByDateAndMealType(LocalDate date, MealType mealType, String username);
    
    Page<FoodLogDTO> getFoodLogsByDateRange(LocalDate startDate, LocalDate endDate, String username, Pageable pageable);

    Page<FoodLogDTO> searchFoodLogs(String query, String username, Pageable pageable);
    
    double getTotalCaloriesForDate(LocalDate date, String username);
    
    Map<MealType, Double> getCaloriesByMealType(LocalDate date, String username);
    
    Map<LocalDate, Double> getCaloriesForDateRange(LocalDate startDate, LocalDate endDate, String username);
    
    NutritionSummaryDTO getNutritionSummaryForDate(LocalDate date, String username);
    
    Map<LocalDate, NutritionSummaryDTO> getNutritionSummaryForDateRange(LocalDate startDate, LocalDate endDate, String username);
    
    Page<FoodLogDTO> getRecentFoodLogs(String username, Pageable pageable);
    
    Page<FoodLogDTO> getFrequentlyLoggedFoods(String username, Pageable pageable);
}
