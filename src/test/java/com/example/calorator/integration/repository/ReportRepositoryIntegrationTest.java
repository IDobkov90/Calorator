package com.example.calorator.integration.repository;

import com.example.calorator.model.entity.Report;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.ReportType;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReportRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReportRepository reportRepository;

    private User testUser1;
    private User testUser2;
    private Report testReport1;
    private Report testReport2;
    private Report testReport3;
    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {

        reportRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        startDate = LocalDate.now().minusDays(30);
        endDate = LocalDate.now();

        testUser1 = new User();
        testUser1.setUsername("testuser1");
        testUser1.setEmail("test1@example.com");
        testUser1.setPassword("password123");
        testUser1.setRole(UserRole.USER);
        entityManager.persist(testUser1);

        testUser2 = new User();
        testUser2.setUsername("testuser2");
        testUser2.setEmail("test2@example.com");
        testUser2.setPassword("password456");
        testUser2.setRole(UserRole.USER);
        entityManager.persist(testUser2);

        testReport1 = new Report();
        testReport1.setUser(testUser1);
        testReport1.setReportType(ReportType.WEEKLY);
        testReport1.setStartDate(startDate);
        testReport1.setEndDate(startDate.plusDays(7));
        testReport1.setAverageCalories(2000.0);
        testReport1.setWeightChange(-1.0);
        entityManager.persist(testReport1);

        testReport2 = new Report();
        testReport2.setUser(testUser1);
        testReport2.setReportType(ReportType.MONTHLY);
        testReport2.setStartDate(startDate);
        testReport2.setEndDate(endDate);
        testReport2.setAverageCalories(2200.0);
        testReport2.setWeightChange(-2.5);
        entityManager.persist(testReport2);

        testReport3 = new Report();
        testReport3.setUser(testUser2);
        testReport3.setReportType(ReportType.WEEKLY);
        testReport3.setStartDate(startDate.plusDays(7));
        testReport3.setEndDate(startDate.plusDays(14));
        testReport3.setAverageCalories(2500.0);
        testReport3.setWeightChange(1.0);
        entityManager.persist(testReport3);

        entityManager.flush();
    }

    @Test
    void findById_ShouldReturnReport() {

        Optional<Report> result = reportRepository.findById(testReport1.getId());

        assertTrue(result.isPresent());
        assertEquals(testReport1.getReportType(), result.get().getReportType());
        assertEquals(testReport1.getStartDate(), result.get().getStartDate());
        assertEquals(testReport1.getEndDate(), result.get().getEndDate());
        assertEquals(testReport1.getAverageCalories(), result.get().getAverageCalories());
        assertEquals(testReport1.getWeightChange(), result.get().getWeightChange());
        assertEquals(testUser1.getId(), result.get().getUser().getId());
    }

    @Test
    void findByUser_ShouldReturnUserReports() {

        List<Report> results = reportRepository.findByUser(testUser1);

        assertThat(results).hasSize(2);
        assertThat(results).extracting(Report::getId)
                .containsExactlyInAnyOrder(testReport1.getId(), testReport2.getId());
    }

    @Test
    void findByUser_WithUserHavingNoReports_ShouldReturnEmptyList() {

        User userWithNoReports = new User();
        userWithNoReports.setUsername("noreports");
        userWithNoReports.setEmail("noreports@example.com");
        userWithNoReports.setPassword("password");
        userWithNoReports.setRole(UserRole.USER);
        entityManager.persist(userWithNoReports);
        entityManager.flush();

        List<Report> results = reportRepository.findByUser(userWithNoReports);

        assertThat(results).isEmpty();
    }

    @Test
    void save_ShouldPersistNewReport() {

        Report newReport = new Report();
        newReport.setUser(testUser2);
        newReport.setReportType(ReportType.WEEKLY);
        newReport.setStartDate(startDate.minusDays(14));
        newReport.setEndDate(startDate.minusDays(1));
        newReport.setAverageCalories(2300.0);
        newReport.setWeightChange(0.5);

        Report savedReport = reportRepository.save(newReport);
        entityManager.flush();

        assertNotNull(savedReport.getId());

        Optional<Report> retrievedReport = reportRepository.findById(savedReport.getId());
        assertTrue(retrievedReport.isPresent());
        assertEquals(ReportType.WEEKLY, retrievedReport.get().getReportType());
        assertEquals(startDate.minusDays(14), retrievedReport.get().getStartDate());
        assertEquals(startDate.minusDays(1), retrievedReport.get().getEndDate());
        assertEquals(2300.0, retrievedReport.get().getAverageCalories());
        assertEquals(0.5, retrievedReport.get().getWeightChange());
        assertEquals(testUser2.getId(), retrievedReport.get().getUser().getId());
    }

    @Test
    void update_ShouldUpdateExistingReport() {

        testReport1.setReportType(ReportType.MONTHLY);
        testReport1.setAverageCalories(2100.0);
        testReport1.setWeightChange(-1.5);

        Report updatedReport = reportRepository.save(testReport1);
        entityManager.flush();
        entityManager.clear();

        Optional<Report> result = reportRepository.findById(testReport1.getId());
        assertTrue(result.isPresent());
        assertEquals(ReportType.MONTHLY, result.get().getReportType());
        assertEquals(2100.0, result.get().getAverageCalories());
        assertEquals(-1.5, result.get().getWeightChange());
    }

    @Test
    void findAll_ShouldReturnAllReports() {

        List<Report> reports = reportRepository.findAll();

        assertThat(reports).hasSize(3);
        assertThat(reports).extracting(Report::getId)
                .containsExactlyInAnyOrder(testReport1.getId(), testReport2.getId(), testReport3.getId());
    }

    @Test
    void deleteById_ShouldRemoveReport() {

        Long idToDelete = testReport1.getId();

        reportRepository.deleteById(idToDelete);
        entityManager.flush();

        Optional<Report> result = reportRepository.findById(idToDelete);
        assertFalse(result.isPresent());

        assertEquals(2, reportRepository.count());
    }

    @Test
    void deleteAll_ShouldRemoveAllReports() {

        reportRepository.deleteAll();
        entityManager.flush();

        assertEquals(0, reportRepository.count());
    }
}