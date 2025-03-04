package com.example.calorator.mapper;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodItemMapper {
    FoodItemDTO toDto(FoodItem entity);

    FoodItem toEntity(FoodItemDTO dto);
}
