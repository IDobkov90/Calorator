package com.example.calorator.mapper;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodItemMapper {
    
    @Mapping(source = "category", target = "category")
    @Mapping(source = "servingUnit", target = "servingUnit")
    FoodItem toEntity(FoodItemDTO dto);
    
    @Mapping(source = "category", target = "category")
    @Mapping(source = "servingUnit", target = "servingUnit")
    FoodItemDTO toDto(FoodItem entity);
    
    default FoodCategory mapFoodCategory(String value) {
        return FoodCategory.fromString(value);
    }
    
    default String map(FoodCategory category) {
        return category != null ? category.name() : null;
    }
    
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
    
    default String map(ServingUnit servingUnit) {
        return servingUnit != null ? servingUnit.name() : null;
    }

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(FoodItemDTO dto, @MappingTarget FoodItem entity);
}
