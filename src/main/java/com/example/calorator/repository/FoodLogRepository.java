package com.example.calorator.repository;

import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.MealType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {

    @Query("SELECT fl FROM FoodLog fl WHERE fl.user = :user " +
           "GROUP BY fl.id, fl.foodItem.id, fl.amount, fl.date, fl.mealType, fl.totalCalories " +
           "ORDER BY COUNT(fl.id) DESC")
    Page<FoodLog> findMostFrequentFoodLogsByUser(User user, Pageable pageable);

    List<FoodLog> findByUserAndDate(User user, LocalDate date);

    List<FoodLog> findByUserAndDateAndMealType(User user, LocalDate date, MealType mealType);

    Page<FoodLog> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate, Pageable pageable);

    @Query("SELECT fl FROM FoodLog fl WHERE fl.user = :user AND LOWER(fl.foodItem.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<FoodLog> findByUserAndFoodItemNameContainingIgnoreCase(@Param("user") User user, @Param("query") String query, Pageable pageable);

    Optional<FoodLog> findByIdAndUser(Long id, User user);

    @Query("SELECT SUM(fl.totalCalories) FROM FoodLog fl WHERE fl.user = :user AND fl.date = :date")
    double sumTotalCaloriesByUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT fl.mealType, SUM(fl.totalCalories) FROM FoodLog fl WHERE fl.user = :user AND fl.date = :date GROUP BY fl.mealType")
    List<Object[]> sumCaloriesByMealTypeForUserAndDate(@Param("user") User user, @Param("date") LocalDate date);

    @Query("SELECT fl.date, SUM(fl.totalCalories) FROM FoodLog fl WHERE fl.user = :user AND fl.date BETWEEN :startDate AND :endDate GROUP BY fl.date")
    List<Object[]> sumCaloriesByDateForUserAndDateRange(@Param("user") User user, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    Page<FoodLog> findByUserOrderByDateDescCreatedAtDesc(User user, Pageable pageable);

    List<FoodLog> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
}