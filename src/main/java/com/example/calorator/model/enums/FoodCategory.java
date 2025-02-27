package com.example.calorator.model.enums;

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

    FoodCategory(String displayName) {
        this.displayName = displayName;
    }
}
