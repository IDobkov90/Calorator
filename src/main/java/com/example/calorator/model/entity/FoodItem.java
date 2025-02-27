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
    @Column
    private double calories;
    @Column
    private double protein;
    @Column
    private double carbs;
    @Column
    private double fat;
    @Enumerated(EnumType.STRING)
    private FoodCategory category;
}
