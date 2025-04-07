package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.dto.FoodLogDTO;
import com.example.calorator.model.dto.NutritionSummaryDTO;
import com.example.calorator.model.enums.MealType;
import com.example.calorator.service.FoodItemService;
import com.example.calorator.service.FoodLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FoodLogControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodLogService foodLogService;

    @MockitoBean
    private FoodItemService foodItemService;

    private FoodLogDTO testFoodLog;
    private List<FoodLogDTO> testFoodLogs;
    private NutritionSummaryDTO testNutritionSummary;
    private List<FoodItemDTO> testFoodItems;
    private LocalDate testDate;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.now();

        FoodItemDTO foodItem1 = new FoodItemDTO();
        foodItem1.setId(1L);
        foodItem1.setName("Apple");

        FoodItemDTO foodItem2 = new FoodItemDTO();
        foodItem2.setId(2L);
        foodItem2.setName("Chicken Breast");

        testFoodItems = Arrays.asList(foodItem1, foodItem2);

        testFoodLog = new FoodLogDTO();
        testFoodLog.setId(1L);
        testFoodLog.setFoodItemId(1L);
        testFoodLog.setDate(testDate);
        testFoodLog.setMealType(MealType.BREAKFAST);
        testFoodLog.setAmount(1.0);

        FoodLogDTO foodLog2 = new FoodLogDTO();
        foodLog2.setId(2L);
        foodLog2.setFoodItemId(2L);
        foodLog2.setDate(testDate);
        foodLog2.setMealType(MealType.LUNCH);
        foodLog2.setAmount(2.0);

        testFoodLogs = Arrays.asList(testFoodLog, foodLog2);

        testNutritionSummary = new NutritionSummaryDTO();
        testNutritionSummary.setTotalCalories(300.0);
        testNutritionSummary.setTotalProtein(25.0);
        testNutritionSummary.setTotalCarbs(30.0);
        testNutritionSummary.setTotalFat(10.0);
    }

    @Test
    @WithAnonymousUser
    void getAllFoodLogs_Unauthenticated_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/food-logs"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAllFoodLogs_Authenticated_ShouldReturnFoodLogsView() throws Exception {
        when(foodLogService.getFoodLogsByDate(any(LocalDate.class), eq("testuser")))
                .thenReturn(testFoodLogs);
        when(foodLogService.getNutritionSummaryForDate(any(LocalDate.class), eq("testuser")))
                .thenReturn(testNutritionSummary);
        when(foodLogService.getRecentFoodLogs(eq("testuser"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(testFoodLogs));

        mockMvc.perform(get("/food-logs")
                        .param("date", testDate.toString())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-logs/list"))
                .andExpect(model().attributeExists("foodLogs"))
                .andExpect(model().attributeExists("nutritionSummary"))
                .andExpect(model().attributeExists("selectedDate"))
                .andExpect(model().attributeExists("recentLogs"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getCreateForm_ShouldReturnCreateView() throws Exception {
        when(foodItemService.getAllFoodItems()).thenReturn(testFoodItems);

        mockMvc.perform(get("/food-logs/create")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-logs/create"))
                .andExpect(model().attributeExists("foodLog"))
                .andExpect(model().attributeExists("foodItems"))
                .andExpect(model().attributeExists("today"))
                .andExpect(model().attributeExists("mealTypes"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void createFoodLog_ValidData_ShouldRedirectToFoodLogs() throws Exception {

        when(foodLogService.createFoodLog(any(), eq("testuser")))
                .thenReturn(testFoodLog);

        mockMvc.perform(post("/food-logs/create")
                        .param("foodItemId", "1")
                        .param("amount", "1.0")
                        .param("date", testDate.toString())
                        .param("mealType", "LUNCH")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/food-logs?date=*"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getEditForm_ExistingFoodLog_ShouldReturnEditView() throws Exception {
        when(foodLogService.getFoodLogById(1L, "testuser"))
                .thenReturn(testFoodLog);
        when(foodItemService.getAllFoodItems()).thenReturn(testFoodItems);

        mockMvc.perform(get("/food-logs/edit/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-logs/edit"))
                .andExpect(model().attributeExists("foodLog"))
                .andExpect(model().attributeExists("foodItems"))
                .andExpect(model().attributeExists("mealTypes"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateFoodLog_ValidData_ShouldRedirectToFoodLogs() throws Exception {
        when(foodLogService.updateFoodLog(eq(1L), any(FoodLogDTO.class), eq("testuser"))).thenReturn(testFoodLog);
        when(foodLogService.getFoodLogById(eq(1L), eq("testuser"))).thenReturn(testFoodLog);

        mockMvc.perform(post("/food-logs/edit/1")
                        .param("id", "1")
                        .param("foodItemId", "1")
                        .param("amount", "1.0")
                        .param("mealType", "LUNCH")
                        .param("date", testDate.toString())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/food-logs?date=" + testDate));
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteFoodLog_ExistingFoodLog_ShouldRedirectToFoodLogs() throws Exception {
        when(foodLogService.getFoodLogById(1L, "testuser"))
                .thenReturn(testFoodLog);

        mockMvc.perform(get("/food-logs/delete/1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/food-logs?date=*"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoodLogsByMealType_ShouldReturnMealView() throws Exception {

        FoodLogDTO breakfastLog = new FoodLogDTO();
        breakfastLog.setId(1L);
        breakfastLog.setFoodItemId(1L);
        breakfastLog.setFoodItemName("Apple");
        breakfastLog.setDate(testDate);
        breakfastLog.setMealType(MealType.BREAKFAST);
        breakfastLog.setAmount(1.0);
        breakfastLog.setTotalCalories(100.0);
        breakfastLog.setTotalProtein(5.0);
        breakfastLog.setTotalCarbs(20.0);
        breakfastLog.setTotalFat(1.0);

        List<FoodLogDTO> breakfastLogs = List.of(breakfastLog);

        when(foodLogService.getFoodLogsByDateAndMealType(eq(testDate), eq(MealType.BREAKFAST), eq("testuser")))
                .thenReturn(breakfastLogs);

        mockMvc.perform(get("/food-logs/meal/BREAKFAST")
                        .param("date", testDate.toString())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-logs/meal-view"))
                .andExpect(model().attributeExists("foodLogs"))
                .andExpect(model().attributeExists("nutritionSummary"))
                .andExpect(model().attributeExists("selectedDate"))
                .andExpect(model().attributeExists("selectedMealType"))
                .andExpect(model().attribute("selectedMealType", MealType.BREAKFAST))
                .andExpect(model().attribute("selectedDate", testDate));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoodLogHistory_ShouldReturnHistoryView() throws Exception {
        LocalDate startDate = testDate.minusDays(7);
        LocalDate endDate = testDate;

        when(foodLogService.getFoodLogsByDateRange(
                eq(startDate),
                eq(endDate),
                eq("testuser"),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(testFoodLogs));

        Map<LocalDate, NutritionSummaryDTO> nutritionSummaries = new HashMap<>();
        nutritionSummaries.put(testDate, testNutritionSummary);
        when(foodLogService.getNutritionSummaryForDateRange(
                eq(startDate),
                eq(endDate),
                eq("testuser")))
                .thenReturn(nutritionSummaries);

        mockMvc.perform(get("/food-logs/history")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-logs/history"))
                .andExpect(model().attributeExists("foodLogs"))
                .andExpect(model().attributeExists("nutritionSummaries"))
                .andExpect(model().attributeExists("startDate"))
                .andExpect(model().attributeExists("endDate"));
    }
}