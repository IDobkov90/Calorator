package com.example.calorator.service.impl;

import com.example.calorator.mapper.ReportMapper;
import com.example.calorator.model.dto.ReportDTO;
import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.entity.Report;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.MealType;
import com.example.calorator.model.enums.ReportType;
import com.example.calorator.repository.FoodLogRepository;
import com.example.calorator.repository.ReportRepository;
import com.example.calorator.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FoodLogRepository foodLogRepository;

    @Mock
    private ReportMapper reportMapper;

    @InjectMocks
    private ReportServiceImpl reportService;

    private User testUser;
    private Report testReport;
    private ReportDTO testReportDTO;
    private FoodLog testFoodLog1;
    private FoodLog testFoodLog2;
    private LocalDate startDate;
    private LocalDate endDate;
    private final String testUsername = "testuser";

    @BeforeEach
    void setUp() {

        startDate = LocalDate.now().minusDays(7);
        endDate = LocalDate.now();

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(testUsername);

        testFoodLog1 = new FoodLog();
        testFoodLog1.setId(1L);
        testFoodLog1.setUser(testUser);
        testFoodLog1.setDate(startDate);
        testFoodLog1.setMealType(MealType.BREAKFAST);
        testFoodLog1.setTotalCalories(1000.0);

        testFoodLog2 = new FoodLog();
        testFoodLog2.setId(2L);
        testFoodLog2.setUser(testUser);
        testFoodLog2.setDate(endDate);
        testFoodLog2.setMealType(MealType.LUNCH);
        testFoodLog2.setTotalCalories(1500.0);

        testReport = new Report();
        testReport.setId(1L);
        testReport.setUser(testUser);
        testReport.setReportType(ReportType.WEEKLY);
        testReport.setStartDate(startDate);
        testReport.setEndDate(endDate);
        testReport.setAverageCalories(312.5);
        testReport.setWeightChange(0.0);

        testReportDTO = new ReportDTO();
        testReportDTO.setId(1L);
        testReportDTO.setReportType(ReportType.WEEKLY);
        testReportDTO.setStartDate(startDate);
        testReportDTO.setEndDate(endDate);
        testReportDTO.setAverageCalories(312.5);
        testReportDTO.setWeightChange(0.0);
    }

    @Test
    void generateReport_WithValidData_ShouldReturnReportDTO() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByUserAndDateBetween(testUser, startDate, endDate))
                .thenReturn(Arrays.asList(testFoodLog1, testFoodLog2));
        when(reportRepository.save(any(Report.class))).thenReturn(testReport);
        when(reportMapper.toDto(testReport)).thenReturn(testReportDTO);

        ReportDTO result = reportService.generateReport(testUsername, ReportType.WEEKLY, startDate, endDate);

        assertNotNull(result);
        assertEquals(testReportDTO.getId(), result.getId());
        assertEquals(testReportDTO.getReportType(), result.getReportType());
        assertEquals(testReportDTO.getStartDate(), result.getStartDate());
        assertEquals(testReportDTO.getEndDate(), result.getEndDate());
        assertEquals(testReportDTO.getAverageCalories(), result.getAverageCalories());

        verify(userRepository).findByUsername(testUsername);
        verify(foodLogRepository).findByUserAndDateBetween(testUser, startDate, endDate);
        verify(reportRepository).save(any(Report.class));
        verify(reportMapper).toDto(testReport);
    }

    @Test
    void generateReport_WithNoFoodLogs_ShouldReturnReportWithZeroCalories() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(foodLogRepository.findByUserAndDateBetween(testUser, startDate, endDate))
                .thenReturn(Collections.emptyList());

        Report emptyReport = new Report();
        emptyReport.setId(1L);
        emptyReport.setUser(testUser);
        emptyReport.setReportType(ReportType.WEEKLY);
        emptyReport.setStartDate(startDate);
        emptyReport.setEndDate(endDate);
        emptyReport.setAverageCalories(0.0);
        emptyReport.setWeightChange(0.0);

        ReportDTO emptyReportDTO = new ReportDTO();
        emptyReportDTO.setId(1L);
        emptyReportDTO.setReportType(ReportType.WEEKLY);
        emptyReportDTO.setStartDate(startDate);
        emptyReportDTO.setEndDate(endDate);
        emptyReportDTO.setAverageCalories(0.0);
        emptyReportDTO.setWeightChange(0.0);

        when(reportRepository.save(any(Report.class))).thenReturn(emptyReport);
        when(reportMapper.toDto(emptyReport)).thenReturn(emptyReportDTO);

        ReportDTO result = reportService.generateReport(testUsername, ReportType.WEEKLY, startDate, endDate);

        assertNotNull(result);
        assertEquals(0.0, result.getAverageCalories());

        verify(userRepository).findByUsername(testUsername);
        verify(foodLogRepository).findByUserAndDateBetween(testUser, startDate, endDate);
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void generateReport_WithUserNotFound_ShouldThrowException() {

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reportService.generateReport("nonexistentuser", ReportType.WEEKLY, startDate, endDate)
        );
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByUsername("nonexistentuser");
        verify(reportRepository, never()).save(any(Report.class));
    }

    @Test
    void getUserReports_WithValidUsername_ShouldReturnListOfReports() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(reportRepository.findByUser(testUser)).thenReturn(Collections.singletonList(testReport));
        when(reportMapper.toDto(testReport)).thenReturn(testReportDTO);

        List<ReportDTO> result = reportService.getUserReports(testUsername);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReportDTO.getId(), result.getFirst().getId());

        verify(userRepository).findByUsername(testUsername);
        verify(reportRepository).findByUser(testUser);
        verify(reportMapper).toDto(testReport);
    }

    @Test
    void getUserReports_WithNoReports_ShouldReturnEmptyList() {

        when(userRepository.findByUsername(testUsername)).thenReturn(Optional.of(testUser));
        when(reportRepository.findByUser(testUser)).thenReturn(Collections.emptyList());

        List<ReportDTO> result = reportService.getUserReports(testUsername);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findByUsername(testUsername);
        verify(reportRepository).findByUser(testUser);
        verify(reportMapper, never()).toDto(any(Report.class));
    }

    @Test
    void getUserReports_WithUserNotFound_ShouldThrowException() {

        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reportService.getUserReports("nonexistentuser")
        );
        assertEquals("User not found", exception.getMessage());

        verify(userRepository).findByUsername("nonexistentuser");
        verify(reportRepository, never()).findByUser(any(User.class));
    }

    @Test
    void getReportById_WithValidId_ShouldReturnReportDTO() {

        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        when(reportMapper.toDto(testReport)).thenReturn(testReportDTO);

        ReportDTO result = reportService.getReportById(1L);

        assertNotNull(result);
        assertEquals(testReportDTO.getId(), result.getId());

        verify(reportRepository).findById(1L);
        verify(reportMapper).toDto(testReport);
    }

    @Test
    void getReportById_WithInvalidId_ShouldThrowException() {

        when(reportRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> reportService.getReportById(999L)
        );
        assertEquals("Report not found", exception.getMessage());

        verify(reportRepository).findById(999L);
        verify(reportMapper, never()).toDto(any(Report.class));
    }

    @Test
    void deleteReport_WithValidId_ShouldDeleteReport() {

        when(reportRepository.findById(1L)).thenReturn(Optional.of(testReport));
        doNothing().when(reportRepository).delete(testReport);

        reportService.deleteReport(1L);

        verify(reportRepository).findById(1L);
        verify(reportRepository).delete(testReport);
    }
}