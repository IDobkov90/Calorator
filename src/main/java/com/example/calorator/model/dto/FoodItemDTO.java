package com.example.calorator.model.dto;

import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItemDTO {
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "Calories are required")
    @Positive(message = "Calories must be positive")
    private Double calories;
    @NotNull(message = "Protein content is required")
    @Positive(message = "Protein must be positive")
    private Double protein;
    @NotNull(message = "Carbs content is required")
    @Positive(message = "Carbs must be positive")
    private Double carbs;
    @NotNull(message = "Fat content is required")
    @Positive(message = "Fat must be positive")
    private Double fat;
    @NotNull(message = "Serving size is required")
    @Positive(message = "Serving size must be positive")
    private Double servingSize;
    @NotNull(message = "Serving unit is required")
    private ServingUnit servingUnit;
    @NotNull(message = "Category is required")
    private FoodCategory category;
}
