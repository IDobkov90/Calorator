package com.example.calorator.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GoalType {
    LOSE_WEIGHT(-500, "Weight Loss"),
    MAINTAIN_WEIGHT(0, "Weight Maintenance"),
    GAIN_WEIGHT(500, "Weight Gain");

    private final int calorieAdjustment;
    private final String description;

}
