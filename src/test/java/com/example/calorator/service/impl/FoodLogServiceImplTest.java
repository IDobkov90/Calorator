package com.example.calorator.service.impl;

import com.example.calorator.mapper.FoodLogMapper;
import com.example.calorator.model.dto.FoodLogDTO;
import com.example.calorator.model.dto.NutritionSummaryDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.MealType;
import com.example.calorator.repository.FoodItemRepository;
import com.example.calorator.repository.FoodLogRepository;
import com.example.calorator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodLogServiceImplTest {

    @Mock
    private FoodLogRepository foodLogRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private FoodLogMapper foodLogMapper;

    @InjectMocks
    private FoodLogServiceImpl foodLogService;

    private User testUser;
    private FoodItem testFoodItem;
    private FoodLog testFoodLog;
    private FoodLogDTO testFoodLogDTO;
    private final String testUsername = "testuser";
    private final LocalDate testDate = LocalDate.now();

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(testUsername);

        testFoodItem = new FoodItem();
        testFoodItem.setId(1L);
        testFoodItem.setName("Test Food");
        testFoodItem.setCalories(100.0);
        testFoodItem.setProtein(10.0);
        testFoodItem.setCarbs(20.0);
        testFoodItem.setFat(5.0);
        testFoodItem.setServingSize(1.0);

        testFoodLog = new FoodLog();
        testFoodLog.setId(1L);
        testFoodLog.setUser(testUser);
        testFoodLog.setFoodItem(testFoodItem);
        testFoodLog.setDate(testDate);
        testFoodLog.setMealType(MealType.BREAKFAST);
        testFoodLog.setAmount(2.0);
        testFoodLog.setTotalCalories(200.0);

        testFoodLogDTO = new FoodLogDTO();
        testFoodLogDTO.setId(1L);
        testFoodLogDTO.setFoodItemId(1L);
        testFoodLogDTO.setDate(testDate);
        testFoodLogDTO.setMealType(MealType.BREAKFAST);
        testFoodLogDTO.setAmount(2.0);
        testFoodLogDTO.setTotalCalories(200.0);
    }

    @Test
    void createFoodLog_ShouldReturnCreatedFoodLog() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(testFoodItem));

        FoodLog newFoodLog = new FoodLog();
        newFoodLog.setFoodItem(testFoodItem);
        newFoodLog.setDate(testFoodLogDTO.getDate());
        newFoodLog.setMealType(testFoodLogDTO.getMealType());
        newFoodLog.setAmount(testFoodLogDTO.getAmount());
        
        when(foodLogMapper.toEntity(testFoodLogDTO)).thenReturn(newFoodLog);
        when(foodLogRepository.save(any(FoodLog.class))).thenReturn(testFoodLog);
        when(foodLogMapper.toDto(testFoodLog)).thenReturn(testFoodLogDTO);

        FoodLogDTO result = foodLogService.createFoodLog(testFoodLogDTO, testUsername);

        assertNotNull(result);
        assertEquals(testFoodLogDTO.getId(), result.getId());
        verify(foodLogRepository).save(any(FoodLog.class));
    }

    @Test
    void updateFoodLog_ShouldReturnUpdatedFoodLog() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(testFoodItem));
        when(foodLogRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testFoodLog));
        when(foodLogRepository.save(any(FoodLog.class))).thenReturn(testFoodLog);
        when(foodLogMapper.toDto(testFoodLog)).thenReturn(testFoodLogDTO);

        FoodLogDTO result = foodLogService.updateFoodLog(1L, testFoodLogDTO, testUsername);

        assertNotNull(result);
        assertEquals(testFoodLogDTO.getId(), result.getId());
        verify(foodLogRepository).save(any(FoodLog.class));
    }

    @Test
    void deleteFoodLog_ShouldDeleteFoodLog() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testFoodLog));

        foodLogService.deleteFoodLog(1L, testUsername);

        verify(foodLogRepository).delete(testFoodLog);
    }

    @Test
    void getFoodLogById_ShouldReturnFoodLog() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testFoodLog));
        when(foodLogMapper.toDto(testFoodLog)).thenReturn(testFoodLogDTO);

        FoodLogDTO result = foodLogService.getFoodLogById(1L, testUsername);

        assertNotNull(result);
        assertEquals(testFoodLogDTO.getId(), result.getId());
    }

    @Test
    void getFoodLogsByDate_ShouldReturnListOfFoodLogs() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByUserAndDate(testUser, testDate))
                .thenReturn(Collections.singletonList(testFoodLog));
        when(foodLogMapper.toDto(testFoodLog)).thenReturn(testFoodLogDTO);

        List<FoodLogDTO> result = foodLogService.getFoodLogsByDate(testDate, testUsername);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFoodLogDTO.getId(), result.getFirst().getId());
    }

    @Test
    void getFoodLogsByDateAndMealType_ShouldReturnListOfFoodLogs() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByUserAndDateAndMealType(testUser, testDate, MealType.BREAKFAST))
                .thenReturn(Collections.singletonList(testFoodLog));
        when(foodLogMapper.toDto(testFoodLog)).thenReturn(testFoodLogDTO);

        List<FoodLogDTO> result = foodLogService.getFoodLogsByDateAndMealType(testDate, MealType.BREAKFAST, testUsername);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFoodLogDTO.getId(), result.getFirst().getId());
    }

    @Test
    void getFoodLogsByDateRange_ShouldReturnPageOfFoodLogs() {

        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;
        Pageable pageable = PageRequest.of(0, 10);
        Page<FoodLog> foodLogPage = new PageImpl<>(Collections.singletonList(testFoodLog));

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByUserAndDateBetween(testUser, startDate, endDate, pageable))
                .thenReturn(foodLogPage);
        when(foodLogMapper.toDto(any(FoodLog.class))).thenReturn(testFoodLogDTO);

        Page<FoodLogDTO> result = foodLogService.getFoodLogsByDateRange(startDate, endDate, testUsername, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testFoodLogDTO.getId(), result.getContent().getFirst().getId());
    }

    @Test
    void getTotalCaloriesForDate_ShouldCalculateCorrectly() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.sumTotalCaloriesByUserAndDate(testUser, testDate))
                .thenReturn(200.0);

        double result = foodLogService.getTotalCaloriesForDate(testDate, testUsername);

        assertEquals(200.0, result, 0.01);
    }

    @Test
    void getCaloriesByMealType_ShouldReturnCorrectMap() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));

        List<Object[]> mockResults = new ArrayList<>();
        mockResults.add(new Object[]{MealType.BREAKFAST, 200.0});

        when(foodLogRepository.sumCaloriesByMealTypeForUserAndDate(testUser, testDate))
                .thenReturn(mockResults);

        Map<MealType, Double> result = foodLogService.getCaloriesByMealType(testDate, testUsername);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.containsKey(MealType.BREAKFAST));
        assertEquals(200.0, result.get(MealType.BREAKFAST), 0.01);
    }

    @Test
    void getNutritionSummaryForDate_ShouldReturnCorrectSummary() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByUserAndDate(testUser, testDate))
                .thenReturn(Collections.singletonList(testFoodLog));

        NutritionSummaryDTO result = foodLogService.getNutritionSummaryForDate(testDate, testUsername);

        assertNotNull(result);
        assertEquals(200.0, result.getTotalCalories(), 0.01);
        assertEquals(20.0, result.getTotalProtein(), 0.01);
        assertEquals(40.0, result.getTotalCarbs(), 0.01);
        assertEquals(10.0, result.getTotalFat(), 0.01);
    }
}