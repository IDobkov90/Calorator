package com.example.calorator.model.entity;

import com.example.calorator.model.enums.GoalType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "goals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType type;
    @Column(name = "target_weight", nullable = false)
    private double targetWeight;
    @Column(name = "daily_calorie_goal", nullable = false)
    private double dailyCalorieGoal;
}
