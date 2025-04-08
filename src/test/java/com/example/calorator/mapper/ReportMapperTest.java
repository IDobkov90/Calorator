package com.example.calorator.mapper;

import com.example.calorator.model.dto.ReportDTO;
import com.example.calorator.model.entity.Report;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.ReportType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReportMapperTest {

    private final ReportMapper reportMapper = Mappers.getMapper(ReportMapper.class);

    @Test
    void toDto_shouldMapReportToReportDTO() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Report report = new Report();
        report.setId(1L);
        report.setUser(user);
        report.setReportType(ReportType.WEEKLY);
        report.setStartDate(LocalDate.of(2023, 1, 1));
        report.setEndDate(LocalDate.of(2023, 1, 7));
        report.setAverageCalories(2000.0);
        report.setWeightChange(-1.5);

        ReportDTO reportDTO = reportMapper.toDto(report);

        assertNotNull(reportDTO);
        assertEquals(1L, reportDTO.getId());
        assertEquals("testuser", reportDTO.getUsername());
        assertEquals(ReportType.WEEKLY, reportDTO.getReportType());
        assertEquals(LocalDate.of(2023, 1, 1), reportDTO.getStartDate());
        assertEquals(LocalDate.of(2023, 1, 7), reportDTO.getEndDate());
        assertEquals(2000.0, reportDTO.getAverageCalories());
        assertEquals(-1.5, reportDTO.getWeightChange());
    }

    @Test
    void toEntity_shouldMapReportDTOToReport() {

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(1L);
        reportDTO.setUsername("testuser");
        reportDTO.setReportType(ReportType.MONTHLY);
        reportDTO.setStartDate(LocalDate.of(2023, 1, 1));
        reportDTO.setEndDate(LocalDate.of(2023, 1, 31));
        reportDTO.setAverageCalories(2200.0);
        reportDTO.setWeightChange(-2.5);

        Report report = reportMapper.toEntity(reportDTO);

        assertNotNull(report);
        assertEquals(1L, report.getId());
        assertNull(report.getUser()); // User should be ignored in mapping
        assertEquals(ReportType.MONTHLY, report.getReportType());
        assertEquals(LocalDate.of(2023, 1, 1), report.getStartDate());
        assertEquals(LocalDate.of(2023, 1, 31), report.getEndDate());
        assertEquals(2200.0, report.getAverageCalories());
        assertEquals(-2.5, report.getWeightChange());
    }

    @Test
    void updateEntityFromDto_shouldUpdateReportFromReportDTO() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setUser(user);
        existingReport.setReportType(ReportType.WEEKLY);
        existingReport.setStartDate(LocalDate.of(2023, 1, 1));
        existingReport.setEndDate(LocalDate.of(2023, 1, 7));
        existingReport.setAverageCalories(2000.0);
        existingReport.setWeightChange(-1.5);

        ReportDTO updateDTO = new ReportDTO();
        updateDTO.setReportType(ReportType.MONTHLY);
        updateDTO.setStartDate(LocalDate.of(2023, 1, 1));
        updateDTO.setEndDate(LocalDate.of(2023, 1, 31));
        updateDTO.setAverageCalories(2200.0);
        updateDTO.setWeightChange(-2.5);

        reportMapper.updateEntityFromDto(updateDTO, existingReport);

        assertEquals(1L, existingReport.getId());
        assertSame(user, existingReport.getUser());
        assertEquals(ReportType.MONTHLY, existingReport.getReportType());
        assertEquals(LocalDate.of(2023, 1, 1), existingReport.getStartDate());
        assertEquals(LocalDate.of(2023, 1, 31), existingReport.getEndDate());
        assertEquals(2200.0, existingReport.getAverageCalories());
        assertEquals(-2.5, existingReport.getWeightChange());
    }

    @Test
    void updateEntityFromDto_withNullValues_shouldNotUpdateFields() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setUser(user);
        existingReport.setReportType(ReportType.WEEKLY);
        existingReport.setStartDate(LocalDate.of(2023, 1, 1));
        existingReport.setEndDate(LocalDate.of(2023, 1, 7));
        existingReport.setAverageCalories(2000.0);
        existingReport.setWeightChange(-1.5);

        ReportDTO updateDTO = new ReportDTO();

        updateDTO.setAverageCalories(2000.0);
        updateDTO.setWeightChange(-1.5);

        reportMapper.updateEntityFromDto(updateDTO, existingReport);

        assertEquals(1L, existingReport.getId());
        assertSame(user, existingReport.getUser());
        assertEquals(ReportType.WEEKLY, existingReport.getReportType());
        assertEquals(LocalDate.of(2023, 1, 1), existingReport.getStartDate());
        assertEquals(LocalDate.of(2023, 1, 7), existingReport.getEndDate());
        assertEquals(2000.0, existingReport.getAverageCalories(), 0.001);
        assertEquals(-1.5, existingReport.getWeightChange(), 0.001);
    }

    @Test
    void updateEntityFromDto_withPartialValues_shouldUpdateOnlyProvidedFields() {

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Report existingReport = new Report();
        existingReport.setId(1L);
        existingReport.setUser(user);
        existingReport.setReportType(ReportType.WEEKLY);
        existingReport.setStartDate(LocalDate.of(2023, 1, 1));
        existingReport.setEndDate(LocalDate.of(2023, 1, 7));
        existingReport.setAverageCalories(2000.0);
        existingReport.setWeightChange(-1.5);

        ReportDTO updateDTO = new ReportDTO();

        updateDTO.setReportType(ReportType.MONTHLY);
        updateDTO.setEndDate(LocalDate.of(2023, 1, 31));

        updateDTO.setAverageCalories(2000.0);
        updateDTO.setWeightChange(-1.5);

        reportMapper.updateEntityFromDto(updateDTO, existingReport);

        assertEquals(1L, existingReport.getId());
        assertSame(user, existingReport.getUser());
        assertEquals(ReportType.MONTHLY, existingReport.getReportType());
        assertEquals(LocalDate.of(2023, 1, 1), existingReport.getStartDate());
        assertEquals(LocalDate.of(2023, 1, 31), existingReport.getEndDate());
        assertEquals(2000.0, existingReport.getAverageCalories(), 0.001);
        assertEquals(-1.5, existingReport.getWeightChange(), 0.001);
    }
}