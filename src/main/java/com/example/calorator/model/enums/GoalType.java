package com.example.calorator.model.enums;

public enum GoalType {
    LOSE_WEIGHT(-500, "Weight Loss"),
    MAINTAIN_WEIGHT(0, "Weight Maintenance"),
    GAIN_WEIGHT(500, "Weight Gain");

    private final int calorieAdjustment;
    private final String description;

    GoalType(int calorieAdjustment, String description) {
        this.calorieAdjustment = calorieAdjustment;
        this.description = description;
    }

    public int getCalorieAdjustment() {
        return calorieAdjustment;
    }

    public String getDescription() {
        return description;
    }
}
