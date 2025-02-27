package com.example.calorator.model.entity;

import com.example.calorator.model.enums.FoodCategory;
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
public class FoodItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false , unique = true)
    private String name;
    @Column(nullable = false)
    private double calories;
    @Column(nullable = false)
    private double protein;
    @Column(nullable = false)
    private double carbs;
    @Column(nullable = false)
    private double fat;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FoodCategory category;
}
