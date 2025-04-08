package com.example.calorator.mapper;

import com.example.calorator.model.dto.FoodLogDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.MealType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FoodLogMapperTest {

    private final FoodLogMapper mapper = Mappers.getMapper(FoodLogMapper.class);

    @Test
    void toDto_ShouldMapAllFields() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        FoodItem foodItem = new FoodItem();
        foodItem.setId(2L);
        foodItem.setName("Apple");

        FoodLog entity = new FoodLog();
        entity.setId(3L);
        entity.setUser(user);
        entity.setFoodItem(foodItem);
        entity.setDate(LocalDate.of(2023, 10, 15));
        entity.setMealType(MealType.BREAKFAST);
        entity.setAmount(2.0);
        entity.setTotalCalories(104.0);

        FoodLogDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(3L);
        assertThat(dto.getUserId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getFoodItemId()).isEqualTo(2L);
        assertThat(dto.getFoodItemName()).isEqualTo("Apple");
        assertThat(dto.getDate()).isEqualTo(LocalDate.of(2023, 10, 15));
        assertThat(dto.getMealType()).isEqualTo(MealType.BREAKFAST);
        assertThat(dto.getAmount()).isEqualTo(2.0);
        assertThat(dto.getTotalCalories()).isEqualTo(104.0);
    }

    @Test
    void toEntity_ShouldMapAllFields() {

        FoodLogDTO dto = new FoodLogDTO();
        dto.setId(3L);
        dto.setUserId(1L);
        dto.setUsername("testuser");
        dto.setFoodItemId(2L);
        dto.setFoodItemName("Apple");
        dto.setDate(LocalDate.of(2023, 10, 15));
        dto.setMealType(MealType.BREAKFAST);
        dto.setAmount(2.0);
        dto.setTotalCalories(104.0);

        FoodLog entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(3L);
        assertThat(entity.getUser()).isNull(); // User is ignored in the mapping
        assertThat(entity.getFoodItem()).isNull(); // FoodItem is ignored in the mapping
        assertThat(entity.getDate()).isEqualTo(LocalDate.of(2023, 10, 15));
        assertThat(entity.getMealType()).isEqualTo(MealType.BREAKFAST);
        assertThat(entity.getAmount()).isEqualTo(2.0);
        assertThat(entity.getTotalCalories()).isEqualTo(104.0);
    }

    @Test
    void updateFoodLogFromDto_ShouldUpdateAllFields() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        FoodItem foodItem = new FoodItem();
        foodItem.setId(2L);
        foodItem.setName("Apple");

        FoodLog entity = new FoodLog();
        entity.setId(3L);
        entity.setUser(user);
        entity.setFoodItem(foodItem);
        entity.setDate(LocalDate.of(2023, 10, 15));
        entity.setMealType(MealType.BREAKFAST);
        entity.setAmount(1.0);
        entity.setTotalCalories(52.0);

        FoodLogDTO dto = new FoodLogDTO();
        dto.setDate(LocalDate.of(2023, 10, 16));
        dto.setMealType(MealType.LUNCH);
        dto.setAmount(2.0);
        dto.setTotalCalories(104.0);

        mapper.updateFoodLogFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(3L);
        assertThat(entity.getUser()).isEqualTo(user);
        assertThat(entity.getFoodItem()).isEqualTo(foodItem);
        assertThat(entity.getDate()).isEqualTo(LocalDate.of(2023, 10, 16));
        assertThat(entity.getMealType()).isEqualTo(MealType.LUNCH);
        assertThat(entity.getAmount()).isEqualTo(2.0);
        assertThat(entity.getTotalCalories()).isEqualTo(104.0);
    }

    @ParameterizedTest
    @EnumSource(MealType.class)
    void mealTypeToString_WithValidMealType_ShouldReturnString(MealType mealType) {

        assertThat(mapper.mealTypeToString(mealType)).isEqualTo(mealType.name());
    }

    @Test
    void mealTypeToString_WithNull_ShouldReturnNull() {

        assertThat(mapper.mealTypeToString(null)).isNull();
    }

    @ParameterizedTest
    @EnumSource(MealType.class)
    void stringToMealType_WithValidString_ShouldReturnMealType(MealType mealType) {

        assertThat(mapper.stringToMealType(mealType.name())).isEqualTo(mealType);
    }

    @Test
    void stringToMealType_WithNull_ShouldReturnNull() {

        assertThat(mapper.stringToMealType(null)).isNull();
    }

    @Test
    void stringToMealType_WithInvalidString_ShouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> mapper.stringToMealType("INVALID_MEAL_TYPE"));
    }

    @Test
    void updateFoodLogFromDto_WithNullValues_ShouldNotUpdateFields() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        FoodItem foodItem = new FoodItem();
        foodItem.setId(2L);
        foodItem.setName("Apple");

        FoodLog entity = new FoodLog();
        entity.setId(3L);
        entity.setUser(user);
        entity.setFoodItem(foodItem);
        entity.setDate(LocalDate.of(2023, 10, 15));
        entity.setMealType(MealType.BREAKFAST);
        entity.setAmount(1.0);
        entity.setTotalCalories(52.0);

        FoodLogDTO dto = new FoodLogDTO();

        mapper.updateFoodLogFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(3L);
        assertThat(entity.getUser()).isEqualTo(user);
        assertThat(entity.getFoodItem()).isEqualTo(foodItem);
        assertThat(entity.getDate()).isEqualTo(LocalDate.of(2023, 10, 15));
        assertThat(entity.getMealType()).isEqualTo(MealType.BREAKFAST);
        assertThat(entity.getAmount()).isEqualTo(1.0);
        assertThat(entity.getTotalCalories()).isEqualTo(52.0);
    }
}