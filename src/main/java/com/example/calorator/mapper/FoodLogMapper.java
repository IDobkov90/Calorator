package com.example.calorator.mapper;

import com.example.calorator.model.dto.FoodLogDTO;
import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.enums.MealType;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {FoodItemMapper.class})
public interface FoodLogMapper {

    @Mapping(target = "foodItemId", source = "foodItem.id")
    @Mapping(target = "foodItemName", source = "foodItem.name")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "mealType", source = "mealType", qualifiedByName = "mealTypeToString")
    FoodLogDTO toDto(FoodLog foodLog);

    @Mapping(target = "foodItem", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "mealType", source = "mealType", qualifiedByName = "stringToMealType")
    FoodLog toEntity(FoodLogDTO foodLogDTO);

    @Named("mealTypeToString")
    default String mealTypeToString(MealType mealType) {
        return mealType != null ? mealType.name() : null;
    }

    @Named("stringToMealType")
    default MealType stringToMealType(String mealType) {
        return mealType != null ? MealType.valueOf(mealType) : null;
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "foodItem", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "mealType", source = "mealType", qualifiedByName = "stringToMealType")
    void updateFoodLogFromDto(FoodLogDTO foodLogDTO, @MappingTarget FoodLog foodLog);
}