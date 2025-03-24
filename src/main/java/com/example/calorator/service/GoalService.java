package com.example.calorator.service;

import com.example.calorator.model.dto.GoalDTO;

public interface GoalService {
    GoalDTO getGoalByUsername(String username);
    void updateGoal(String username, GoalDTO goalDTO);
    void deleteGoal(String username);
}