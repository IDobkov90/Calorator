package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.ReportDTO;
import com.example.calorator.model.enums.ReportType;
import com.example.calorator.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ReportControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReportService reportService;

    private ReportDTO testReport1;
    private ReportDTO testReport2;
    private List<ReportDTO> testReports;
    private final LocalDate startDate = LocalDate.now().minusDays(7);
    private final LocalDate endDate = LocalDate.now();

    @BeforeEach
    void setUp() {
        testReport1 = new ReportDTO();
        testReport1.setId(1L);
        testReport1.setReportType(ReportType.WEEKLY);
        testReport1.setStartDate(startDate);
        testReport1.setEndDate(endDate);
        testReport1.setAverageCalories(2000.0);
        testReport1.setWeightChange(-1.0);

        testReport2 = new ReportDTO();
        testReport2.setId(2L);
        testReport2.setReportType(ReportType.MONTHLY);
        testReport2.setStartDate(startDate.minusDays(23));
        testReport2.setEndDate(endDate);
        testReport2.setAverageCalories(2200.0);
        testReport2.setWeightChange(-2.5);

        testReports = Arrays.asList(testReport1, testReport2);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getReportsPage_AuthenticatedUser_ShouldReturnReportsList() throws Exception {
        when(reportService.getUserReports("testuser")).thenReturn(testReports);

        mockMvc.perform(get("/reports"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("reports/list"))
                .andExpect(model().attributeExists("reports"))
                .andExpect(model().attribute("reports", hasSize(2)))
                .andExpect(model().attribute("reports", contains(
                        hasProperty("id", is(1L)),
                        hasProperty("id", is(2L))
                )));

        verify(reportService, times(1)).getUserReports("testuser");
    }

    @Test
    @WithAnonymousUser
    void getReportsPage_UnauthenticatedUser_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/reports"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(reportService, never()).getUserReports(any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getReportDetails_ValidId_ShouldReturnDetailsView() throws Exception {
        when(reportService.getReportById(1L)).thenReturn(testReport1);

        mockMvc.perform(get("/reports/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("reports/details"))
                .andExpect(model().attributeExists("report"))
                .andExpect(model().attribute("report", hasProperty("id", is(1L))))
                .andExpect(model().attribute("report", hasProperty("reportType", is(ReportType.WEEKLY))))
                .andExpect(model().attribute("report", hasProperty("averageCalories", is(2000.0))));

        verify(reportService, times(1)).getReportById(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void getGenerateReportForm_ShouldReturnGenerateForm() throws Exception {
        mockMvc.perform(get("/reports/generate"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("reports/generate"))
                .andExpect(model().attributeExists("reportTypes"))
                .andExpect(model().attribute("reportTypes", arrayContaining(ReportType.values())));
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateReport_ValidData_ShouldRedirectToReportDetails() throws Exception {
        when(reportService.generateReport(
                eq("testuser"),
                eq(ReportType.WEEKLY),
                any(LocalDate.class),
                any(LocalDate.class)
        )).thenReturn(testReport1);

        mockMvc.perform(post("/reports/generate")
                        .param("reportType", "WEEKLY")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reports/1"));

        verify(reportService, times(1)).generateReport(
                eq("testuser"),
                eq(ReportType.WEEKLY),
                any(LocalDate.class),
                any(LocalDate.class)
        );
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteReport_ValidId_ShouldReturnNoContent() throws Exception {
        doNothing().when(reportService).deleteReport(1L);

        mockMvc.perform(delete("/reports/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(reportService, times(1)).deleteReport(1L);
    }

    @Test
    @WithAnonymousUser
    void deleteReport_UnauthenticatedUser_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(delete("/reports/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(reportService, never()).deleteReport(anyLong());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getReportsPage_NoReports_ShouldReturnEmptyList() throws Exception {
        when(reportService.getUserReports("testuser")).thenReturn(List.of());

        mockMvc.perform(get("/reports"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("reports/list"))
                .andExpect(model().attributeExists("reports"))
                .andExpect(model().attribute("reports", hasSize(0)));

        verify(reportService, times(1)).getUserReports("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void generateReport_InvalidDateRange_ShouldHandleError() throws Exception {
        when(reportService.generateReport(
                eq("testuser"),
                eq(ReportType.WEEKLY),
                eq(endDate),
                eq(startDate)
        )).thenThrow(new IllegalArgumentException("End date must be after start date"));

        mockMvc.perform(post("/reports/generate")
                        .param("reportType", "WEEKLY")
                        .param("startDate", endDate.toString())
                        .param("endDate", startDate.toString())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(view().name("error/500"));

        verify(reportService, times(1)).generateReport(
                eq("testuser"),
                eq(ReportType.WEEKLY),
                eq(endDate),
                eq(startDate)
        );
    }
}