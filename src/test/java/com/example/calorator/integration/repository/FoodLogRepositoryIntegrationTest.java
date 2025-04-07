package com.example.calorator.integration.repository;

import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.MealType;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.FoodLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class FoodLogRepositoryIntegrationTest {

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

        foodLogRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setRole(UserRole.USER);
        entityManager.persist(testUser);

        FoodItem testFoodItem = new FoodItem();
        testFoodItem.setName("Test Food");
        testFoodItem.setCalories(100.0);
        testFoodItem.setProtein(10.0);
        testFoodItem.setCarbs(20.0);
        testFoodItem.setFat(5.0);
        testFoodItem.setServingSize(1.0);
        testFoodItem.setServingUnit(ServingUnit.GRAM);
        testFoodItem.setCategory(FoodCategory.OTHER);
        entityManager.persist(testFoodItem);

        today = LocalDate.now();
        yesterday = today.minusDays(1);

        testFoodLog1 = new FoodLog();
        testFoodLog1.setUser(testUser);
        testFoodLog1.setFoodItem(testFoodItem);
        testFoodLog1.setDate(today);
        testFoodLog1.setMealType(MealType.BREAKFAST);
        testFoodLog1.setAmount(1.0);
        testFoodLog1.setTotalCalories(300.0);
        entityManager.persist(testFoodLog1);

        testFoodLog2 = new FoodLog();
        testFoodLog2.setUser(testUser);
        testFoodLog2.setFoodItem(testFoodItem);
        testFoodLog2.setDate(today);
        testFoodLog2.setMealType(MealType.LUNCH);
        testFoodLog2.setAmount(1.0);
        testFoodLog2.setTotalCalories(500.0);
        entityManager.persist(testFoodLog2);

        testFoodLog3 = new FoodLog();
        testFoodLog3.setUser(testUser);
        testFoodLog3.setFoodItem(testFoodItem);
        testFoodLog3.setDate(yesterday);
        testFoodLog3.setMealType(MealType.DINNER);
        testFoodLog3.setAmount(1.0);
        testFoodLog3.setTotalCalories(400.0);
        entityManager.persist(testFoodLog3);

        entityManager.flush();
    }

    @Test
    void findByUserId_ShouldReturnAllUserFoodLogs() {

        List<FoodLog> result = foodLogRepository.findByUserAndDateBetween(
                testUser, yesterday.minusDays(1), today.plusDays(1));

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
    void findByUserAndDateAndMealType_ShouldReturnFoodLogsForSpecificDateAndMealType() {

        List<FoodLog> result = foodLogRepository.findByUserAndDateAndMealType(
                testUser, today, MealType.LUNCH);

        assertThat(result).hasSize(1);
        assertThat(result).contains(testFoodLog2);
    }

    @Test
    void findByIdAndUser_ShouldReturnFoodLogByIdAndUser() {

        Optional<FoodLog> result = foodLogRepository.findByIdAndUser(testFoodLog1.getId(), testUser);

        assertTrue(result.isPresent());
        assertEquals(testFoodLog1, result.get());
    }

    @Test
    void findByUserOrderByDateDescCreatedAtDesc_ShouldReturnOrderedResults() {

        Page<FoodLog> result = foodLogRepository.findByUserOrderByDateDescCreatedAtDesc(
                testUser, PageRequest.of(0, 2));

        assertThat(result.getContent()).hasSize(2);

        assertThat(result.getTotalElements()).isEqualTo(3);
    }

    @Test
    void sumTotalCaloriesByUserAndDate_ShouldReturnCorrectSum() {

        double result = foodLogRepository.sumTotalCaloriesByUserAndDate(testUser, today);


        assertEquals(200.0, result);
    }

    @Test
    void sumCaloriesByMealTypeForUserAndDate_ShouldReturnCorrectSummary() {

        List<Object[]> results = foodLogRepository.sumCaloriesByMealTypeForUserAndDate(testUser, today);

        assertThat(results).hasSize(2); // BREAKFAST and LUNCH

        Map<MealType, Double> caloriesByMealType = new HashMap<>();
        for (Object[] result : results) {
            MealType mealType = (MealType) result[0];
            Double calories = (Double) result[1];
            caloriesByMealType.put(mealType, calories);
        }

        assertEquals(100.0, caloriesByMealType.get(MealType.BREAKFAST));
        assertEquals(100.0, caloriesByMealType.get(MealType.LUNCH));
    }

    @Test
    void findMostFrequentFoodLogsByUser_ShouldReturnResults() {

        Page<FoodLog> result = foodLogRepository.findMostFrequentFoodLogsByUser(
                testUser, PageRequest.of(0, 10));

        assertThat(result.getContent()).isNotEmpty();
    }
}