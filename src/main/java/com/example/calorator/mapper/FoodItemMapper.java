package com.example.calorator.mapper;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FoodItemMapper {
    FoodItemDTO toDto(FoodItem entity);

    @Mapping(target = "id", ignore = true)
    FoodItem toEntity(FoodItemDTO dto);

    void updateEntityFromDto(FoodItemDTO dto, @MappingTarget FoodItem entity);

    List<FoodItemDTO> toDtoList(List<FoodItem> entities);
}
