package com.example.calorator.service.impl;

import com.example.calorator.mapper.GoalMapper;
import com.example.calorator.model.dto.GoalDTO;
import com.example.calorator.model.entity.Goal;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.repository.GoalRepository;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;
    private final GoalMapper goalMapper;

    @Override
    public GoalDTO getGoalByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Goal goal = goalRepository.findByUser(user)
                .orElse(null);
        return goal != null ? goalMapper.toDto(goal) : null;

    }

    @Override
    public void updateGoal(String username, GoalDTO goalDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Goal goal = goalRepository.findByUser(user)
                .orElse(new Goal());

        goalMapper.updateEntityFromDto(goalDTO, goal);
        goal.setUser(user);

        double baseCalories = calculateBaseCalories(user);
        goal.setDailyCalorieGoal(baseCalories + goalDTO.getType().getCalorieAdjustment());

        goalRepository.save(goal);
    }

    @Override
    public void deleteGoal(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        goalRepository.deleteByUser(user);
    }

    private double calculateBaseCalories(User user) {
        UserProfile profile = user.getUserProfile();
        double bmr;
        if (profile.getGender() == Gender.MALE) {
            bmr = 88.362 + (13.397 * profile.getWeight()) + (4.799 * profile.getHeight()) - (5.677 * profile.getAge());
        } else {
            bmr = 447.593 + (9.247 * profile.getWeight()) + (3.098 * profile.getHeight()) - (4.330 * profile.getAge());
        }

        double activityMultiplier = profile.getActivityLevel().getFactor();

        return bmr * activityMultiplier;
    }
}
