package com.example.calorator.model.entity;

import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "food_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private double calories;
    @Column(nullable = false)
    private double protein;
    @Column(nullable = false)
    private double carbs;
    @Column(nullable = false)
    private double fat;
    @Column(nullable = false, name = "serving_size")
    private double servingSize;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "serving_unit")
    private ServingUnit servingUnit;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory category;
}
