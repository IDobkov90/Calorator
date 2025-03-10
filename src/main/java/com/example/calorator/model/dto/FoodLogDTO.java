package com.example.calorator.model.dto;

import com.example.calorator.model.enums.MealType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodLogDTO {
    private Long id;

    @NotNull(message = "Food item is required")
    private Long foodItemId;

    @Size(max = 255, message = "Food name cannot exceed 255 characters")
    private String foodItemName;

    private Long userId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Meal type is required")
    private MealType mealType;

    @NotNull(message = "Date is required")
    @PastOrPresent(message = "Date cannot be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Double totalCalories;
    private Double totalProtein;
    private Double totalCarbs;
    private Double totalFat;

    private String username;

    public void calculateNutrients(double calories, double protein, double carbs, double fat, double amount) {
        this.totalCalories = calories * amount;
        this.totalProtein = protein * amount;
        this.totalCarbs = carbs * amount;
        this.totalFat = fat * amount;
    }
}
