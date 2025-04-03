package com.example.calorator.service.impl;

import com.example.calorator.mapper.GoalMapper;
import com.example.calorator.model.dto.GoalDTO;
import com.example.calorator.model.entity.Goal;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.entity.UserProfile;
import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.model.enums.GoalType;
import com.example.calorator.repository.GoalRepository;
import com.example.calorator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceImplTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GoalMapper goalMapper;

    @InjectMocks
    private GoalServiceImpl goalService;

    private User testUser;
    private UserProfile testUserProfile;
    private Goal testGoal;
    private GoalDTO testGoalDTO;
    private final String testUsername = "testuser";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(testUsername);

        UserProfile testUserProfile = new UserProfile();
        testUserProfile.setId(1L);
        testUserProfile.setAge(30);
        testUserProfile.setWeight(70.0);
        testUserProfile.setHeight(175.0);
        testUserProfile.setGender(Gender.MALE);
        testUserProfile.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        testUser.setUserProfile(testUserProfile);

        testGoal = new Goal();
        testGoal.setId(1L);
        testGoal.setUser(testUser);
        testGoal.setType(GoalType.MAINTAIN_WEIGHT);
        testGoal.setTargetWeight(70.0);
        testGoal.setDailyCalorieGoal(2500.0);

        testGoalDTO = new GoalDTO();
        testGoalDTO.setType(GoalType.MAINTAIN_WEIGHT);
        testGoalDTO.setTargetWeight(70.0);
        testGoalDTO.setDailyCalorieGoal(2500.0);
    }

    @Test
    void getGoalByUsername_WithExistingGoal_ShouldReturnGoalDTO() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUser(testUser)).thenReturn(Optional.of(testGoal));
        when(goalMapper.toDto(testGoal)).thenReturn(testGoalDTO);

        GoalDTO result = goalService.getGoalByUsername(testUsername);

        assertNotNull(result);
        assertEquals(testGoalDTO.getType(), result.getType());
        assertEquals(testGoalDTO.getTargetWeight(), result.getTargetWeight());
        assertEquals(testGoalDTO.getDailyCalorieGoal(), result.getDailyCalorieGoal());

        verify(userRepository).findByUsername(testUsername);
        verify(goalRepository).findByUser(testUser);
        verify(goalMapper).toDto(testGoal);
    }

    @Test
    void getGoalByUsername_WithNoGoal_ShouldReturnNull() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUser(testUser)).thenReturn(Optional.empty());

        GoalDTO result = goalService.getGoalByUsername(testUsername);

        assertNull(result);

        verify(userRepository).findByUsername(testUsername);
        verify(goalRepository).findByUser(testUser);
        verify(goalMapper, never()).toDto(any(Goal.class));
    }

    @Test
    void getGoalByUsername_WithUserNotFound_ShouldThrowException() {

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> goalService.getGoalByUsername("nonexistentuser")
        );
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByUsername("nonexistentuser");
        verify(goalRepository, never()).findByUser(any(User.class));
    }

    @Test
    void updateGoal_WithExistingGoal_ShouldUpdateGoal() {

        GoalDTO updateGoalDTO = new GoalDTO();
        updateGoalDTO.setType(GoalType.LOSE_WEIGHT);
        updateGoalDTO.setTargetWeight(65.0);

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUser(testUser)).thenReturn(Optional.of(testGoal));
        doNothing().when(goalMapper).updateEntityFromDto(updateGoalDTO, testGoal);
        when(goalRepository.save(any(Goal.class))).thenReturn(testGoal);

        goalService.updateGoal(testUsername, updateGoalDTO);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(goalCaptor.capture());
        Goal savedGoal = goalCaptor.getValue();

        assertEquals(testUser, savedGoal.getUser());

        assertTrue(savedGoal.getDailyCalorieGoal() < 2614 && savedGoal.getDailyCalorieGoal() > 2000);

        verify(userRepository).findByUsername(testUsername);
        verify(goalRepository).findByUser(testUser);
        verify(goalMapper).updateEntityFromDto(updateGoalDTO, testGoal);
    }

    @Test
    void updateGoal_WithNoExistingGoal_ShouldCreateNewGoal() {

        GoalDTO newGoalDTO = new GoalDTO();
        newGoalDTO.setType(GoalType.GAIN_WEIGHT);
        newGoalDTO.setTargetWeight(75.0);

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUser(testUser)).thenReturn(Optional.empty());
        doNothing().when(goalMapper).updateEntityFromDto(eq(newGoalDTO), any(Goal.class));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        goalService.updateGoal(testUsername, newGoalDTO);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(goalCaptor.capture());
        Goal savedGoal = goalCaptor.getValue();

        assertEquals(testUser, savedGoal.getUser());

        assertTrue(savedGoal.getDailyCalorieGoal() > 2614 && savedGoal.getDailyCalorieGoal() < 3200);

        verify(userRepository).findByUsername(testUsername);
        verify(goalRepository).findByUser(testUser);
        verify(goalMapper).updateEntityFromDto(eq(newGoalDTO), any(Goal.class));
    }

    @Test
    void updateGoal_WithUserNotFound_ShouldThrowException() {

        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setType(GoalType.MAINTAIN_WEIGHT);
        goalDTO.setTargetWeight(70.0);

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> goalService.updateGoal("nonexistentuser", goalDTO)
        );
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByUsername("nonexistentuser");
        verify(goalRepository, never()).findByUser(any(User.class));
        verify(goalRepository, never()).save(any(Goal.class));
    }

    @Test
    void deleteGoal_WithExistingUser_ShouldDeleteGoal() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        doNothing().when(goalRepository).deleteByUser(testUser);

        goalService.deleteGoal(testUsername);

        verify(userRepository).findByUsername(testUsername);
        verify(goalRepository).deleteByUser(testUser);
    }

    @Test
    void deleteGoal_WithUserNotFound_ShouldThrowException() {

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> goalService.deleteGoal("nonexistentuser")
        );
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByUsername("nonexistentuser");
        verify(goalRepository, never()).deleteByUser(any(User.class));
    }

    @Test
    void calculateBaseCalories_ForMale_ShouldCalculateCorrectly() {

        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setType(GoalType.MAINTAIN_WEIGHT);
        goalDTO.setTargetWeight(70.0);

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(goalRepository.findByUser(testUser)).thenReturn(Optional.empty());
        doNothing().when(goalMapper).updateEntityFromDto(eq(goalDTO), any(Goal.class));
        when(goalRepository.save(any(Goal.class))).thenAnswer(invocation -> invocation.getArgument(0));

        goalService.updateGoal(testUsername, goalDTO);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(goalCaptor.capture());
        Goal savedGoal = goalCaptor.getValue();

        assertTrue(savedGoal.getDailyCalorieGoal() > 2500 && savedGoal.getDailyCalorieGoal() < 2700);
    }
}