package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.GoalDTO;
import com.example.calorator.model.enums.GoalType;
import com.example.calorator.service.GoalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class GoalControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoalService goalService;

    private GoalDTO testGoal;

    @BeforeEach
    void setUp() {
        testGoal = new GoalDTO();
        testGoal.setType(GoalType.LOSE_WEIGHT);
        testGoal.setTargetWeight(70.0);
        testGoal.setDailyCalorieGoal(2000.0);
    }

    @Test
    @WithMockUser(username = "testuser")
    void viewGoal_AuthenticatedUser_ShouldReturnGoalView() throws Exception {
        when(goalService.getGoalByUsername("testuser")).thenReturn(testGoal);

        mockMvc.perform(get("/goals"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("goals/view"))
                .andExpect(model().attributeExists("goal"))
                .andExpect(model().attribute("goal", testGoal));

        verify(goalService, times(1)).getGoalByUsername("testuser");
    }

    @Test
    @WithAnonymousUser
    void viewGoal_UnauthenticatedUser_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/goals"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(goalService, never()).getGoalByUsername(any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void editGoalForm_ExistingGoal_ShouldReturnEditFormWithGoal() throws Exception {
        when(goalService.getGoalByUsername("testuser")).thenReturn(testGoal);

        mockMvc.perform(get("/goals/edit"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("goals/edit"))
                .andExpect(model().attributeExists("goal"))
                .andExpect(model().attribute("goal", testGoal));

        verify(goalService, times(1)).getGoalByUsername("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void editGoalForm_NoExistingGoal_ShouldReturnEditFormWithEmptyGoal() throws Exception {
        when(goalService.getGoalByUsername("testuser")).thenReturn(null);

        mockMvc.perform(get("/goals/edit"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("goals/edit"))
                .andExpect(model().attributeExists("goal"))
                .andExpect(model().attribute("goal", hasProperty("type", nullValue())))
                .andExpect(model().attribute("goal", hasProperty("targetWeight", is(0.0))));

        verify(goalService, times(1)).getGoalByUsername("testuser");
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_ValidData_ShouldRedirectToGoalsView() throws Exception {

        doNothing().when(goalService).updateGoal(eq("testuser"), any(GoalDTO.class));

        mockMvc.perform(post("/goals/edit")
                        .param("type", "LOSE_WEIGHT")
                        .param("targetWeight", "70.0")
                        .param("dailyCalorieGoal", "2000.0")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goals"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(flash().attribute("message", "Goal updated successfully"));

        verify(goalService, times(1)).updateGoal(eq("testuser"), any(GoalDTO.class));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateGoal_InvalidData_ShouldReturnToEditForm() throws Exception {
        mockMvc.perform(post("/goals/edit")
                        .param("type", "LOSE_WEIGHT")
                        .param("targetWeight", "-10.0") // Invalid negative weight
                        .param("dailyCalorieGoal", "2000.0")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("goals/edit"))
                .andExpect(model().attributeExists("goal"))
                .andExpect(model().hasErrors());

        verify(goalService, never()).updateGoal(any(), any());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteGoal_ShouldRedirectToGoalsView() throws Exception {
        mockMvc.perform(post("/goals/delete")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goals"));

        verify(goalService, times(1)).deleteGoal("testuser");
    }

    @Test
    @WithAnonymousUser
    void deleteGoal_UnauthenticatedUser_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/goals/delete")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(goalService, never()).deleteGoal(any());
    }
}