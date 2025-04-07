package com.example.calorator.integration.repository;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.MealType;
import com.example.calorator.repository.FoodLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FoodLogRepositoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FoodLogRepository foodLogRepository;

    private User testUser;
    private FoodLog testFoodLog1;
    private FoodLog testFoodLog2;
    private FoodLog testFoodLog3;
    private LocalDate today;
    private LocalDate yesterday;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        entityManager.persist(testUser);


        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        entityManager.persist(anotherUser);

        today = LocalDate.now();
        yesterday = today.minusDays(1);

        testFoodLog1 = new FoodLog();
        testFoodLog1.setUser(testUser);
        testFoodLog1.setDate(today);
        testFoodLog1.setMealType(MealType.BREAKFAST);
        testFoodLog1.setTotalCalories(500.0);
        entityManager.persist(testFoodLog1);

        testFoodLog2 = new FoodLog();
        testFoodLog2.setUser(testUser);
        testFoodLog2.setDate(today);
        testFoodLog2.setMealType(MealType.LUNCH);
        testFoodLog2.setTotalCalories(700.0);

        entityManager.persist(testFoodLog2);

        testFoodLog3 = new FoodLog();
        testFoodLog3.setUser(testUser);
        testFoodLog3.setDate(yesterday);
        testFoodLog3.setMealType(MealType.DINNER);
        testFoodLog3.setTotalCalories(600.0);

        entityManager.persist(testFoodLog3);

        FoodLog anotherUserFoodLog = new FoodLog();
        anotherUserFoodLog.setUser(anotherUser);
        anotherUserFoodLog.setDate(today);
        anotherUserFoodLog.setMealType(MealType.BREAKFAST);
        anotherUserFoodLog.setTotalCalories(450.0);

        entityManager.persist(anotherUserFoodLog);

        entityManager.flush();
    }

    @Test
    void findByUser_ShouldReturnAllUserFoodLogs() {

        List<FoodLog> result = foodLogRepository.findByUserAndDateBetween(
                testUser, yesterday, today);

        assertThat(result).hasSize(3);
        assertThat(result).contains(testFoodLog1, testFoodLog2, testFoodLog3);
    }

    @Test
    void findByUserAndDate_ShouldReturnFoodLogsForSpecificDate() {

        List<FoodLog> result = foodLogRepository.findByUserAndDate(testUser, today);

        assertThat(result).hasSize(2);
        assertThat(result).contains(testFoodLog1, testFoodLog2);
        assertThat(result).doesNotContain(testFoodLog3);
    }

    @Test
    void findByUserAndDateBetween_ShouldReturnFoodLogsInDateRange() {

        List<FoodLog> result = foodLogRepository.findByUserAndDateBetween(
                testUser, yesterday, today);

        assertThat(result).hasSize(3);
        assertThat(result).contains(testFoodLog1, testFoodLog2, testFoodLog3);
    }

    @Test
    void findByUserAndDateAndMealType_ShouldReturnSpecificMeal() {

        List<FoodLog> result = foodLogRepository.findByUserAndDateAndMealType(
                testUser, today, MealType.BREAKFAST);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo(testFoodLog1);
    }

    @Test
    void findByUserAndDateAndMealType_WithNonExistentMeal_ShouldReturnEmpty() {

        List<FoodLog> result = foodLogRepository.findByUserAndDateAndMealType(
                testUser, today, MealType.SNACK);

        assertTrue(result.isEmpty());
    }

    @Test
    void findByUserOrderByDateDesc_ShouldReturnOrderedByDateDesc() {

        Page<FoodLog> result = foodLogRepository.findByUserOrderByDateDescCreatedAtDesc(
                testUser, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent().get(0).getDate())
                .isEqualTo(today);
        assertThat(result.getContent().get(2).getDate())
                .isEqualTo(yesterday);
    }

    @Test
    void findByUserAndDateBetweenOrderByDateAsc_ShouldReturnOrderedByDateAsc() {

        List<FoodLog> result = foodLogRepository.findByUserAndDateBetween(
                testUser, yesterday, today);

        List<FoodLog> sortedResult = result.stream()
                .sorted(Comparator.comparing(FoodLog::getDate))
                .collect(Collectors.toList());

        assertThat(sortedResult).hasSize(3);
        assertThat(sortedResult.get(0).getDate()).isEqualTo(yesterday);
        assertThat(sortedResult.get(1).getDate()).isEqualTo(today);
        assertThat(sortedResult.get(2).getDate()).isEqualTo(today);
    }

    @Test
    void deleteByUserAndId_ShouldRemoveSpecificFoodLog() {

        FoodLog foodLogToDelete = foodLogRepository.findById(testFoodLog1.getId()).orElseThrow();
        foodLogRepository.delete(foodLogToDelete);
        entityManager.flush();
        entityManager.clear();

        Optional<FoodLog> deletedFoodLog = foodLogRepository.findById(testFoodLog1.getId());
        assertThat(deletedFoodLog).isEmpty();

        List<FoodLog> remainingLogs = foodLogRepository.findByUserAndDateBetween(
                testUser, yesterday, today);
        assertThat(remainingLogs).hasSize(2);
        assertThat(remainingLogs).contains(testFoodLog2, testFoodLog3);
    }

    @Test
    void calculateTotalCaloriesForUserAndDate_ShouldSumCalories() {

        Double totalCalories = foodLogRepository.sumTotalCaloriesByUserAndDate(
                testUser, today);

        assertThat(totalCalories).isEqualTo(1200.0);
    }

    @Test
    void calculateTotalCaloriesForUserAndDateRange_ShouldSumCalories() {

        List<Object[]> results = foodLogRepository.sumCaloriesByDateForUserAndDateRange(
                testUser, yesterday, today);

        Double totalCalories = results.stream()
                .mapToDouble(result -> (Double) result[1])
                .sum();

        assertThat(totalCalories).isEqualTo(1800.0);
    }

    @Test
    void findByUserWithPagination_ShouldReturnPagedResults() {

        Page<FoodLog> firstPage = foodLogRepository.findByUserAndDateBetween(
                testUser, yesterday, today, PageRequest.of(0, 2, Sort.by("date").descending()));

        assertThat(firstPage.getContent()).hasSize(2);
        assertThat(firstPage.getTotalElements()).isEqualTo(3);
        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getNumber()).isEqualTo(0);

        Page<FoodLog> secondPage = foodLogRepository.findByUserAndDateBetween(
                testUser, yesterday, today, PageRequest.of(1, 2, Sort.by("date").descending()));

        assertThat(secondPage.getContent()).hasSize(1);
        assertThat(secondPage.getTotalElements()).isEqualTo(3);
        assertThat(secondPage.getTotalPages()).isEqualTo(2);
        assertThat(secondPage.getNumber()).isEqualTo(1);
    }
}