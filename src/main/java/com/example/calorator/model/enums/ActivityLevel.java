package com.example.calorator.model.enums;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    SEDENTARY(1.2, "Little or no exercise"),
    LIGHTLY_ACTIVE(1.375, "Light exercise 1-3 days/week"),
    MODERATELY_ACTIVE(1.55, "Moderate exercise 3-5 days/week"),
    VERY_ACTIVE(1.725, "Hard exercise 6-7 days/week"),
    EXTREMELY_ACTIVE(1.9, "Very hard exercise & physical job");

    private final double factor;
    private final String description;

    ActivityLevel(double factor, String description) {
        this.factor = factor;
        this.description = description;
    }
}
