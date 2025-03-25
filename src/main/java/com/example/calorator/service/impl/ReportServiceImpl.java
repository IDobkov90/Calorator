package com.example.calorator.service.impl;

import com.example.calorator.mapper.ReportMapper;
import com.example.calorator.model.dto.ReportDTO;
import com.example.calorator.model.entity.FoodLog;
import com.example.calorator.model.entity.Report;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.ReportType;
import com.example.calorator.repository.FoodLogRepository;
import com.example.calorator.repository.ReportRepository;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.ReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final ReportMapper reportMapper;
    private final FoodLogRepository foodLogRepository;

    @Override
    public ReportDTO generateReport(String username, ReportType reportType, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        double averageCalories = calculateAverageCalories(user, startDate, endDate);
        double weightChange = calculateWeightChange(user, startDate, endDate);

        Report report = new Report();
        report.setUser(user);
        report.setReportType(reportType);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setAverageCalories(averageCalories);
        report.setWeightChange(weightChange);
        Report savedReport = reportRepository.save(report);
        return reportMapper.toDto(savedReport);
    }

    private double calculateAverageCalories(User user, LocalDate startDate, LocalDate endDate) {
        List<FoodLog> foodLogs = foodLogRepository.findByUserAndDateBetween(user, startDate, endDate);
        if (foodLogs.isEmpty()) {
            return 0.0;
        }
        double totalCalories = foodLogs.stream()
                .mapToDouble(FoodLog::getTotalCalories)
                .sum();
        long days = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        return totalCalories / days;
    }

    private double calculateWeightChange(User user, LocalDate startDate, LocalDate endDate) {
        return 0.0;
    }

    @Override
    public List<ReportDTO> getUserReports(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Report> reports = reportRepository.findByUser(user);
        return reports.stream()
                .map(reportMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDTO getReportById(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        return reportMapper.toDto(report);
    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Report not found with id: " + reportId));

        report.setUser(null);
        reportRepository.delete(report);
    }
}
