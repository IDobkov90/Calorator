package com.example.calorator.model.entity;

import com.example.calorator.model.enums.ReportType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Report extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;
    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "report_type")
    private ReportType reportType;
    @Column(nullable = false, name = "average_calories")
    private double averageCalories;
    @Column(nullable = false, name = "weight_change")
    private double weightChange;
}
