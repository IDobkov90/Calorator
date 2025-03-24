package com.example.calorator.mapper;

import com.example.calorator.model.dto.GoalDTO;
import com.example.calorator.model.entity.Goal;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dailyCalorieGoal", ignore = true)
    Goal toEntity(GoalDTO dto);

    GoalDTO toDto(Goal entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "dailyCalorieGoal", ignore = true)
    void updateEntityFromDto(GoalDTO dto, @MappingTarget Goal entity);
}
