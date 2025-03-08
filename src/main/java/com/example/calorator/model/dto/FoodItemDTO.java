package com.example.calorator.model.dto;

import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
    @PositiveOrZero(message = "Protein must be zero or positive")
    private Double protein;
    @NotNull(message = "Carbs content is required")
    @PositiveOrZero(message = "Carbs must be zero or positive")
    private Double carbs;
    @NotNull(message = "Fat content is required")
    @PositiveOrZero(message = "Fat must be zero or positive")
    private Double fat;
    @NotNull(message = "Serving size is required")
    @Positive(message = "Serving size must be positive")
    private Double servingSize;
    @NotNull(message = "Serving unit is required")
    private String servingUnit;
    @NotNull(message = "Category is required")
    private String category;

    public String getCategoryDisplayName() {
        try {
            return FoodCategory.fromString(category).getDisplayName();
        } catch (Exception e) {
            return category;
        }
    }

    public String getServingUnitDisplayName() {
        try {
            return ServingUnit.valueOf(servingUnit).getAbbreviation();
        } catch (Exception e) {
            return servingUnit;
        }
    }
}
