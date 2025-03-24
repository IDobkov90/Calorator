package com.example.calorator.model.dto;

import com.example.calorator.model.enums.GoalType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalDTO {
    @NotNull(message = "Goal type is required")
    private GoalType type;
    @Min(value = 20, message = "Target weight must be at least 20")
    private double targetWeight;
}
