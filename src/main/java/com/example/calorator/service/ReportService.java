package com.example.calorator.service;

import com.example.calorator.model.dto.ReportDTO;
import com.example.calorator.model.enums.ReportType;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    ReportDTO generateReport(String username, ReportType reportType, LocalDate startDate, LocalDate endDate);
    List<ReportDTO> getUserReports(String username);
    ReportDTO getReportById(Long reportId);
    void deleteReport(Long reportId);
}