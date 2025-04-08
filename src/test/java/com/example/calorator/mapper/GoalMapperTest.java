package com.example.calorator.mapper;

import com.example.calorator.model.dto.GoalDTO;
import com.example.calorator.model.entity.Goal;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.GoalType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class GoalMapperTest {

    private final GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    @Test
    void toEntity_shouldMapGoalDTOToGoal() {

        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setType(GoalType.LOSE_WEIGHT);
        goalDTO.setTargetWeight(70.0);
        goalDTO.setDailyCalorieGoal(2000.0);

        Goal goal = goalMapper.toEntity(goalDTO);

        assertNotNull(goal);
        assertEquals(GoalType.LOSE_WEIGHT, goal.getType());
        assertEquals(70.0, goal.getTargetWeight());

        assertEquals(0.0, goal.getDailyCalorieGoal(), 0.001);

        assertNull(goal.getId());
        assertNull(goal.getUser());
    }

    @Test
    void toDto_shouldMapGoalToGoalDTO() {

        Goal goal = new Goal();
        goal.setId(1L);
        goal.setType(GoalType.GAIN_WEIGHT);
        goal.setTargetWeight(85.0);
        goal.setDailyCalorieGoal(3000.0);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        goal.setUser(user);

        GoalDTO goalDTO = goalMapper.toDto(goal);

        assertNotNull(goalDTO);
        assertEquals(GoalType.GAIN_WEIGHT, goalDTO.getType());
        assertEquals(85.0, goalDTO.getTargetWeight());
        assertEquals(3000.0, goalDTO.getDailyCalorieGoal());
    }

    @Test
    void updateEntityFromDto_shouldUpdateGoalFromGoalDTO() {

        Goal existingGoal = new Goal();
        existingGoal.setId(1L);
        existingGoal.setType(GoalType.MAINTAIN_WEIGHT);
        existingGoal.setTargetWeight(75.0);
        existingGoal.setDailyCalorieGoal(2500.0);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        existingGoal.setUser(user);

        GoalDTO updateDTO = new GoalDTO();
        updateDTO.setType(GoalType.LOSE_WEIGHT);
        updateDTO.setTargetWeight(70.0);
        updateDTO.setDailyCalorieGoal(2000.0);

        goalMapper.updateEntityFromDto(updateDTO, existingGoal);

        assertEquals(1L, existingGoal.getId());
        assertEquals(GoalType.LOSE_WEIGHT, existingGoal.getType());
        assertEquals(70.0, existingGoal.getTargetWeight());
        assertEquals(2500.0, existingGoal.getDailyCalorieGoal());
        assertSame(user, existingGoal.getUser());
    }

    @Test
    void updateEntityFromDto_withNullValues_shouldNotUpdateFields() {

        Goal existingGoal = new Goal();
        existingGoal.setId(1L);
        existingGoal.setType(GoalType.MAINTAIN_WEIGHT);
        existingGoal.setTargetWeight(75.0);
        existingGoal.setDailyCalorieGoal(2500.0);

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        existingGoal.setUser(user);

        Goal originalGoal = new Goal();
        originalGoal.setId(existingGoal.getId());
        originalGoal.setType(existingGoal.getType());
        originalGoal.setTargetWeight(existingGoal.getTargetWeight());
        originalGoal.setDailyCalorieGoal(existingGoal.getDailyCalorieGoal());
        originalGoal.setUser(existingGoal.getUser());

        GoalDTO updateDTO = new GoalDTO();
        updateDTO.setType(null);

        updateDTO.setTargetWeight(75.0);
        updateDTO.setDailyCalorieGoal(2000.0); // This should be ignored by the mapper

        goalMapper.updateEntityFromDto(updateDTO, existingGoal);

        assertEquals(originalGoal.getId(), existingGoal.getId());
        assertEquals(originalGoal.getType(), existingGoal.getType()); // Type should not change
        assertEquals(originalGoal.getTargetWeight(), existingGoal.getTargetWeight(), 0.001); // Should match what we set
        assertEquals(originalGoal.getDailyCalorieGoal(), existingGoal.getDailyCalorieGoal(), 0.001); // Should not change
        assertSame(originalGoal.getUser(), existingGoal.getUser());
    }
}
