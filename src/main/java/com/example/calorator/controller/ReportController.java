package com.example.calorator.controller;

import com.example.calorator.model.dto.ReportDTO;
import com.example.calorator.model.enums.ReportType;
import com.example.calorator.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping
    public String getReportsPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        List<ReportDTO> reports = reportService.getUserReports(userDetails.getUsername());
        model.addAttribute("reports", reports);
        return "reports/list";
    }

    @GetMapping("/{id}")
    public String getReportDetails(@PathVariable Long id, Model model) {
        ReportDTO report = reportService.getReportById(id);
        model.addAttribute("report", report);
        return "reports/details";
    }

    @GetMapping("/generate")
    public String getGenerateReportForm(Model model) {
        model.addAttribute("reportTypes", ReportType.values());
        return "reports/generate";
    }

    @PostMapping("/generate")
    public String generateReport(
            @RequestParam ReportType reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReportDTO generatedReport = reportService.generateReport(userDetails.getUsername(), reportType, startDate, endDate);
        return "redirect:/reports/" + generatedReport.getId();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable Long id) {
        reportService.deleteReport(id);
        return ResponseEntity.noContent().build();
    }
}
