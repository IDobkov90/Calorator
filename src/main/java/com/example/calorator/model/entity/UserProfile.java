package com.example.calorator.model.entity;

import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false , name = "first_name")
    private String firstName;
    @Column(nullable = false , name = "last_name")
    private String lastName;
    private int age;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column
    private double weight;
    @Column
    private double height;
    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
