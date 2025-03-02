package com.example.calorator.model.dto;

import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    private String username;
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;
    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotNull(message = "Age is required")
    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 120, message = "Age must be at most 120")
    private Integer age;
    @NotNull(message = "Gender is required")
    private Gender gender;
    @NotNull(message = "Weight is required")
    @DecimalMin(value = "30.0", message = "Weight must be at least 30.0 kg")
    @DecimalMax(value = "300.0", message = "Weight must be at most 300.0 kg")
    private Double weight;
    @NotNull(message = "Height is required")
    @DecimalMin(value = "100.0", message = "Height must be at least 100.0 cm")
    @DecimalMax(value = "250.0", message = "Height must be at most 250.0 cm")
    private Double height;
    @NotNull(message = "Activity level is required")
    private ActivityLevel activityLevel;
}
