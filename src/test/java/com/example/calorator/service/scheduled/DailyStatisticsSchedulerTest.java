package com.example.calorator.service.scheduled;

import com.example.calorator.model.dto.NutritionSummaryDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.FoodLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyStatisticsSchedulerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodLogService foodLogService;

    @InjectMocks
    private DailyStatisticsScheduler scheduler;

    private User user1;
    private User user2;
    private LocalDate yesterday;
    private NutritionSummaryDTO summary1;
    private NutritionSummaryDTO summary2;

    @BeforeEach
    void setUp() {
        yesterday = LocalDate.now().minusDays(1);

        user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");

        user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        summary1 = new NutritionSummaryDTO(yesterday, 2000.0, 100.0, 200.0, 80.0);
        summary2 = new NutritionSummaryDTO(yesterday, 1800.0, 90.0, 180.0, 70.0);
    }

    @Test
    void generateDailyStatistics_WithMultipleUsers_ShouldCalculateAverages() {

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user1")))
                .thenReturn(summary1);
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user2")))
                .thenReturn(summary2);

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user1"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user2"));
    }

    @Test
    void generateDailyStatistics_WithNoUsers_ShouldHandleGracefully() {

        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService, never()).getNutritionSummaryForDate(any(), any());
    }

    @Test
    void generateDailyStatistics_WithNullSummary_ShouldSkipUser() {

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user1")))
                .thenReturn(null);
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user2")))
                .thenReturn(summary2);

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user1"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user2"));
    }

    @Test
    void generateDailyStatistics_WithAllNullSummaries_ShouldHandleGracefully() {

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user1")))
                .thenReturn(null);
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user2")))
                .thenReturn(null);

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user1"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user2"));
    }

    @Test
    void generateDailyStatistics_WithException_ShouldContinueProcessing() {

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user1")))
                .thenThrow(new RuntimeException("Test exception"));
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user2")))
                .thenReturn(summary2);

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user1"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user2"));
    }

    @Test
    void generateDailyStatistics_WithAllExceptions_ShouldHandleGracefully() {

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user1")))
                .thenThrow(new RuntimeException("Test exception 1"));
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user2")))
                .thenThrow(new RuntimeException("Test exception 2"));

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user1"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user2"));
    }

    @Test
    void generateDailyStatistics_WithZeroCalories_ShouldIncludeInAverage() {

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        NutritionSummaryDTO zeroCaloriesSummary = new NutritionSummaryDTO(yesterday, 0.0, 0.0, 0.0, 0.0);
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user1")))
                .thenReturn(zeroCaloriesSummary);
        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user2")))
                .thenReturn(summary2);

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user1"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user2"));
    }

    @Test
    void generateDailyStatistics_WithMixedResults_ShouldHandleCorrectly() {

        User user3 = new User();
        user3.setId(3L);
        user3.setUsername("user3");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2, user3));

        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user1")))
                .thenReturn(summary1);

        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user2")))
                .thenThrow(new RuntimeException("Test exception"));

        when(foodLogService.getNutritionSummaryForDate(eq(yesterday), eq("user3")))
                .thenReturn(null);

        scheduler.generateDailyStatistics();

        verify(userRepository).findAll();
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user1"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user2"));
        verify(foodLogService).getNutritionSummaryForDate(eq(yesterday), eq("user3"));
    }
}