package com.example.calorator.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FoodCategory {
    FRUITS("Fruits"),
    VEGETABLES("Vegetables"),
    GRAINS("Grains"),
    PROTEIN("Protein"),
    DAIRY("Dairy"),
    FATS("Fats & Oils"),
    SWEETS("Sweets & Desserts"),
    BEVERAGES("Beverages"),
    OTHER("Other");

    private final String displayName;
    
    public static FoodCategory fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        
        try {
            return FoodCategory.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            for (FoodCategory category : FoodCategory.values()) {
                if (category.getDisplayName().equalsIgnoreCase(value)) {
                    return category;
                }
            }
            throw new IllegalArgumentException("Invalid food category: " + value);
        }
    }
}
