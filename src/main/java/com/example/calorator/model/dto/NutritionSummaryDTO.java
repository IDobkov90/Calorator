package com.example.calorator.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionSummaryDTO {

    private LocalDate date;
    private Double totalCalories;
    private Double totalProtein;
    private Double totalCarbs;
    private Double totalFat;
    private Map<String, Double> caloriesByMeal;

    public NutritionSummaryDTO(LocalDate date, Double totalCalories, Double totalProtein, Double totalCarbs, Double totalFat) {
        this.date = date;
        this.totalCalories = totalCalories;
        this.totalProtein = totalProtein;
        this.totalCarbs = totalCarbs;
        this.totalFat = totalFat;
        this.caloriesByMeal = new HashMap<>();
    }

    public void addMealCalories(String mealType, Double calories) {
        if (caloriesByMeal == null) {
            caloriesByMeal = new HashMap<>();
        }
        caloriesByMeal.put(mealType, caloriesByMeal.getOrDefault(mealType, 0.0) + calories);
    }

    public Double getMealPercentage(String mealType) {
        if (caloriesByMeal == null || !caloriesByMeal.containsKey(mealType) || totalCalories == null || totalCalories == 0) {
            return 0.0;
        }
        return (caloriesByMeal.get(mealType) / totalCalories) * 100;
    }
}