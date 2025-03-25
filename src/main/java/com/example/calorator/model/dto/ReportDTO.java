package com.example.calorator.model.dto;

import com.example.calorator.model.enums.ReportType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {
    private Long id;
    private String username;
    private LocalDate startDate;
    private LocalDate endDate;
    private ReportType reportType;
    private double averageCalories;
    private double weightChange;
}
