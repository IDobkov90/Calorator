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
import com.example.calorator.service.FoodLogService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodLogServiceImpl implements FoodLogService {

    private final FoodLogRepository foodLogRepository;
    private final UserRepository userRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodLogMapper foodLogMapper;

    @Override
    @Transactional
    public FoodLogDTO createFoodLog(FoodLogDTO foodLogDTO, String username) {
        User user = getUserByUsername(username);
        FoodItem foodItem = getFoodItemById(foodLogDTO.getFoodItemId());

        FoodLog foodLog = foodLogMapper.toEntity(foodLogDTO);
        foodLog.setUser(user);
        foodLog.setFoodItem(foodItem);
        foodLog.setMealType(foodLogDTO.getMealType());

        double totalCalories = calculateTotalCalories(foodItem.getCalories(),
                foodItem.getServingSize(),
                foodLogDTO.getAmount());
        foodLog.setTotalCalories(totalCalories);

        FoodLog savedFoodLog = foodLogRepository.save(foodLog);
        return foodLogMapper.toDto(savedFoodLog);
    }

    @Override
    @Transactional
    public FoodLogDTO updateFoodLog(Long id, FoodLogDTO foodLogDTO, String username) {
        FoodLog existingFoodLog = getFoodLogByIdAndUsername(id, username);
        FoodItem foodItem = getFoodItemById(foodLogDTO.getFoodItemId());

        existingFoodLog.setDate(foodLogDTO.getDate());
        existingFoodLog.setMealType(foodLogDTO.getMealType());
        existingFoodLog.setAmount(foodLogDTO.getAmount());
        existingFoodLog.setFoodItem(foodItem);

        double totalCalories = calculateTotalCalories(foodItem.getCalories(),
                foodItem.getServingSize(),
                foodLogDTO.getAmount());
        existingFoodLog.setTotalCalories(totalCalories);

        FoodLog updatedFoodLog = foodLogRepository.save(existingFoodLog);
        return foodLogMapper.toDto(updatedFoodLog);
    }

    @Override
    @Transactional
    public void deleteFoodLog(Long id, String username) {
        FoodLog foodLog = getFoodLogByIdAndUsername(id, username);
        foodLogRepository.delete(foodLog);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodLogDTO getFoodLogById(Long id, String username) {
        FoodLog foodLog = getFoodLogByIdAndUsername(id, username);
        return foodLogMapper.toDto(foodLog);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodLogDTO> getFoodLogsByDate(LocalDate date, String username) {
        User user = getUserByUsername(username);
        List<FoodLog> foodLogs = foodLogRepository.findByUserAndDate(user, date);
        return foodLogs.stream()
                .map(foodLogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodLogDTO> getFoodLogsByDateAndMealType(LocalDate date, MealType mealType, String username) {
        User user = getUserByUsername(username);
        List<FoodLog> foodLogs = foodLogRepository.findByUserAndDateAndMealType(user, date, mealType);
        return foodLogs.stream()
                .map(foodLogMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodLogDTO> getFoodLogsByDateRange(LocalDate startDate, LocalDate endDate, String username, Pageable pageable) {
        User user = getUserByUsername(username);
        Page<FoodLog> foodLogs = foodLogRepository.findByUserAndDateBetween(user, startDate, endDate, pageable);
        return foodLogs.map(foodLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodLogDTO> searchFoodLogs(String query, String username, Pageable pageable) {
        User user = getUserByUsername(username);
        Page<FoodLog> foodLogs = foodLogRepository.findByUserAndFoodItemNameContainingIgnoreCase(user, query, pageable);
        return foodLogs.map(foodLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public double getTotalCaloriesForDate(LocalDate date, String username) {
        User user = getUserByUsername(username);
        return foodLogRepository.sumTotalCaloriesByUserAndDate(user, date);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<MealType, Double> getCaloriesByMealType(LocalDate date, String username) {
        User user = getUserByUsername(username);
        List<Object[]> results = foodLogRepository.sumCaloriesByMealTypeForUserAndDate(user, date);

        Map<MealType, Double> caloriesByMealType = new HashMap<>();
        for (Object[] result : results) {
            MealType mealType = (MealType) result[0];
            Double calories = (Double) result[1];
            caloriesByMealType.put(mealType, calories);
        }

        return caloriesByMealType;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, Double> getCaloriesForDateRange(LocalDate startDate, LocalDate endDate, String username) {
        User user = getUserByUsername(username);
        List<Object[]> results = foodLogRepository.sumCaloriesByDateForUserAndDateRange(user, startDate, endDate);

        Map<LocalDate, Double> caloriesByDate = new HashMap<>();
        for (Object[] result : results) {
            LocalDate date = (LocalDate) result[0];
            Double calories = (Double) result[1];
            caloriesByDate.put(date, calories);
        }

        return caloriesByDate;
    }

    @Override
    @Transactional(readOnly = true)
    public NutritionSummaryDTO getNutritionSummaryForDate(LocalDate date, String username) {
        User user = getUserByUsername(username);

        List<FoodLog> foodLogs = foodLogRepository.findByUserAndDate(user, date);

        double totalCalories = 0;
        double totalProtein = 0;
        double totalCarbs = 0;
        double totalFat = 0;

        Map<String, Double> caloriesByMeal = new HashMap<>();

        for (FoodLog log : foodLogs) {
            FoodItem foodItem = log.getFoodItem();
            double ratio = log.getAmount() / foodItem.getServingSize();

            totalCalories += log.getTotalCalories();
            totalProtein += foodItem.getProtein() * ratio;
            totalCarbs += foodItem.getCarbs() * ratio;
            totalFat += foodItem.getFat() * ratio;

            String mealType = log.getMealType().name();
            caloriesByMeal.put(mealType,
                    caloriesByMeal.getOrDefault(mealType, 0.0) + log.getTotalCalories());
        }

        NutritionSummaryDTO summary = new NutritionSummaryDTO(
                date, totalCalories, totalProtein, totalCarbs, totalFat);

        caloriesByMeal.forEach(summary::addMealCalories);

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<LocalDate, NutritionSummaryDTO> getNutritionSummaryForDateRange(LocalDate startDate, LocalDate endDate, String username) {
        Map<LocalDate, NutritionSummaryDTO> summaries = new HashMap<>();

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            NutritionSummaryDTO summary = getNutritionSummaryForDate(currentDate, username);
            summaries.put(currentDate, summary);
            currentDate = currentDate.plusDays(1);
        }

        return summaries;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodLogDTO> getRecentFoodLogs(String username, Pageable pageable) {
        User user = getUserByUsername(username);
        Page<FoodLog> foodLogs = foodLogRepository.findByUserOrderByDateDescCreatedAtDesc(user, pageable);
        return foodLogs.map(foodLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodLogDTO> getFrequentlyLoggedFoods(String username, Pageable pageable) {
        User user = getUserByUsername(username);
        Page<FoodLog> frequentFoodLogs = foodLogRepository.findMostFrequentFoodLogsByUser(user, pageable);
        return frequentFoodLogs.map(foodLogMapper::toDto);
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    private FoodItem getFoodItemById(Long id) {
        return foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food item not found with id: " + id));
    }

    private FoodLog getFoodLogByIdAndUsername(Long id, String username) {
        User user = getUserByUsername(username);
        return foodLogRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new EntityNotFoundException("Food log not found with id: " + id));
    }

    private double calculateTotalCalories(double caloriesPerServing, double servingSize, double amount) {
        return (caloriesPerServing / servingSize) * amount;
    }
}