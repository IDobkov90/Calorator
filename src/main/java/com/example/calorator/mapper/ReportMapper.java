package com.example.calorator.mapper;

import com.example.calorator.model.dto.ReportDTO;
import com.example.calorator.model.entity.Report;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReportMapper {
    @Mapping(target = "username", source = "user.username")
    ReportDTO toDto(Report entity);

    @Mapping(target = "user", ignore = true)
    Report toEntity(ReportDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(ReportDTO dto, @MappingTarget Report entity);
}
