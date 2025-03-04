package com.example.calorator.mapper;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodItemMapper {
    FoodItemDTO toDto(FoodItem entity);

    default ServingUnit mapServingUnit(String servingUnitStr) {
        if (servingUnitStr == null || servingUnitStr.isEmpty()) {
            return null;
        }

        try {
            for (ServingUnit unit : ServingUnit.values()) {
                if (unit.getAbbreviation().equalsIgnoreCase(servingUnitStr)) {
                    return unit;
                }
            }

            return ServingUnit.valueOf(servingUnitStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid serving unit: " + servingUnitStr);
        }
    }

    default FoodCategory mapFoodCategory(String value) {
        return FoodCategory.valueOf(value.toUpperCase());
    }

    default String map(ServingUnit unit) {
        return unit.name();
    }

    default String map(FoodCategory category) {
        return category.name();
    }

    @Mapping(target = "servingUnit", source = "servingUnit")
    @Mapping(target = "category", source = "category")
    FoodItem toEntity(FoodItemDTO dto);

    void updateEntityFromDto(FoodItemDTO dto, @MappingTarget FoodItem entity);

    List<FoodItemDTO> toDtoList(List<FoodItem> entities);
}
