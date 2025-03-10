package com.example.calorator.model.entity;

import com.example.calorator.model.enums.MealType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "food_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(FoodLog.FoodLogListener.class)
public class FoodLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private FoodItem foodItem;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "meal_type")
    private MealType mealType;
    
    @Column(nullable = false)
    private double amount;
    
    @Column(nullable = false, name = "total_calories")
    private double totalCalories;

    void calculateCalories() {
        if (foodItem != null && amount > 0) {
            if (foodItem.getServingSize() > 0) {
                this.totalCalories = foodItem.getCalories() * (amount / foodItem.getServingSize());
            } else {
                this.totalCalories = foodItem.getCalories() * amount;
            }
        } else {
            this.totalCalories = 0;
        }
    }

    public FoodLog(User user, FoodItem foodItem, LocalDate date, MealType mealType, double amount) {
        this.user = user;
        this.foodItem = foodItem;
        this.date = date;
        this.mealType = mealType;
        this.amount = amount;
        calculateCalories();
    }

    public static class FoodLogListener {
        @PrePersist
        public void prePersist(FoodLog foodLog) {
            foodLog.calculateCalories();
        }
        
        @PreUpdate
        public void preUpdate(FoodLog foodLog) {
            foodLog.calculateCalories();
        }
    }
}