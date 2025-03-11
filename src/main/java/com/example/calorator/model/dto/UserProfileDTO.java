package com.example.calorator.model.dto;

import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Integer age;
    private Double height;
    private Double weight;
    private ActivityLevel activityLevel;
    private Integer dailyCalorieGoal;
}