package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithAnonymousUser
    void getRegisterPage_ShouldReturnRegisterView() throws Exception {
        mockMvc.perform(get("/register")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/register"))
                .andExpect(model().attributeExists("userRegisterDTO"));
    }

    @Test
    @WithAnonymousUser
    void getLoginPage_ShouldReturnLoginView() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getProfilePage_AuthenticatedUser_ShouldReturnProfileView() throws Exception {
        UserProfileDTO mockProfile = new UserProfileDTO();

        mockProfile.setFirstName("Test");
        mockProfile.setLastName("User");
        mockProfile.setAge(30);
        mockProfile.setGender(Gender.MALE);
        mockProfile.setWeight(80.0);
        mockProfile.setHeight(180.0);
        mockProfile.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);

        when(userService.getUserProfile("testuser")).thenReturn(mockProfile);

        mockMvc.perform(get("/profile")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/profile"))
                .andExpect(model().attributeExists("userProfile"));
    }

    @Test
    void getProfilePage_UnauthenticatedUser_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithAnonymousUser
    void postRegisterUser_ValidData_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "newTestUser")
                        .param("email", "newtest@example.com")
                        .param("password", "password123")
                        .param("confirmPassword", "password123")
                        .param("firstName", "Test")
                        .param("lastName", "User")
                        .param("gender", "MALE")
                        .param("age", "30")
                        .param("height", "180.0")
                        .param("weight", "80.0")
                        .param("activityLevel", "MODERATELY_ACTIVE")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));
    }
}