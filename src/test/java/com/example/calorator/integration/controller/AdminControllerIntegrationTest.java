package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.dto.UserDTO;
import com.example.calorator.model.dto.UserProfileDTO;
import com.example.calorator.model.entity.User;
import com.example.calorator.model.enums.ActivityLevel;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.Gender;
import com.example.calorator.model.enums.UserRole;
import com.example.calorator.repository.UserRepository;
import com.example.calorator.service.FoodItemService;
import com.example.calorator.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private FoodItemService foodItemService;

    @Test
    @WithAnonymousUser
    void adminDashboard_UnauthenticatedUser_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        verify(foodItemService, never()).getAllFoodItems();
        verify(userRepository, never()).count();
    }

    @Test
    @WithMockUser(roles = "USER")
    void adminDashboard_NonAdminUser_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andDo(print())
                .andExpect(status().isForbidden());

        verify(foodItemService, never()).getAllFoodItems();
        verify(userRepository, never()).count();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminDashboard_AdminUser_ShouldReturnDashboardView() throws Exception {

        List<FoodItemDTO> foodItems = Arrays.asList(
                createFoodItemDTO(1L, "Apple"),
                createFoodItemDTO(2L, "Banana")
        );
        Page<FoodItemDTO> foodItemPage = new PageImpl<>(foodItems);

        List<User> users = Arrays.asList(
                createUser(1L, "user1"),
                createUser(2L, "user2")
        );
        Page<User> userPage = new PageImpl<>(users);

        when(foodItemService.getAllFoodItems()).thenReturn(foodItems);
        when(foodItemService.getAllFoodItems(any(Pageable.class))).thenReturn(foodItemPage);
        when(userRepository.count()).thenReturn(2L);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/admin/dashboard"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("foodCategories"))
                .andExpect(model().attributeExists("totalFoodItems"))
                .andExpect(model().attributeExists("totalUsers"))
                .andExpect(model().attributeExists("recentUsers"))
                .andExpect(model().attributeExists("recentFoodItems"))
                .andExpect(model().attribute("totalFoodItems", 2))
                .andExpect(model().attribute("totalUsers", 2L))
                .andExpect(model().attribute("recentUsers", hasSize(2)))
                .andExpect(model().attribute("recentFoodItems", hasSize(2)));

        verify(foodItemService).getAllFoodItems();
        verify(foodItemService).getAllFoodItems(any(Pageable.class));
        verify(userRepository).count();
        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void listUsers_ShouldReturnUsersView() throws Exception {

        List<UserDTO> users = Arrays.asList(
                createUserDTO(1L, "user1", UserRole.USER),
                createUserDTO(2L, "user2", UserRole.USER)
        );
        Page<UserDTO> userPage = new PageImpl<>(users);

        when(userService.getAllUsers(any(Pageable.class))).thenReturn(userPage);

        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/users"))
                .andExpect(model().attributeExists("users"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attribute("users", hasSize(2)))
                .andExpect(model().attribute("currentPage", 0))
                .andExpect(model().attribute("totalPages", 1));

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewUserProfile_ExistingUser_ShouldReturnUserProfileView() throws Exception {

        UserProfileDTO userProfile = createUserProfileDTO("testuser");
        when(userService.getUserProfile("testuser")).thenReturn(userProfile);

        mockMvc.perform(get("/admin/users/testuser"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/user-profile"))
                .andExpect(model().attributeExists("userProfile"))
                .andExpect(model().attribute("userProfile", hasProperty("username", is("testuser"))));

        verify(userService).getUserProfile("testuser");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void viewUserProfile_NonExistingUser_ShouldRedirectWithError() throws Exception {

        when(userService.getUserProfile("nonexistent")).thenReturn(null);

        mockMvc.perform(get("/admin/users/nonexistent"))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users?error=User+not+found"));

        verify(userService).getUserProfile("nonexistent");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void editUserRoleForm_ShouldReturnEditRoleView() throws Exception {

        UserDTO user = createUserDTO(1L, "testuser", UserRole.USER);
        when(userService.getUserByUsername("testuser")).thenReturn(user);
        when(userService.getAllAvailableRoles()).thenReturn(UserRole.values());

        mockMvc.perform(get("/admin/users/testuser/role"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/edit-user-role"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("allRoles"))
                .andExpect(model().attributeExists("currentRole"))
                .andExpect(model().attribute("user", hasProperty("username", is("testuser"))))
                .andExpect(model().attribute("currentRole", is(UserRole.USER)));

        verify(userService).getUserByUsername("testuser");
        verify(userService).getAllAvailableRoles();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_Success_ShouldRedirectWithSuccessMessage() throws Exception {

        mockMvc.perform(post("/admin/users/testuser/role")
                        .param("role", "ADMIN")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attributeExists("successMessage"))
                .andExpect(flash().attribute("successMessage", containsString("updated successfully")));

        verify(userService).updateUserRole("testuser", UserRole.ADMIN);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateUserRole_Error_ShouldRedirectWithErrorMessage() throws Exception {

        doThrow(new RuntimeException("Test error")).when(userService).updateUserRole(anyString(), any(UserRole.class));

        mockMvc.perform(post("/admin/users/testuser/role")
                        .param("role", "ADMIN")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", containsString("Error updating user role")));

        verify(userService).updateUserRole("testuser", UserRole.ADMIN);
    }

    private FoodItemDTO createFoodItemDTO(Long id, String name) {
        FoodItemDTO dto = new FoodItemDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setCalories(100.0);
        dto.setProtein(10.0);
        dto.setCarbs(20.0);
        dto.setFat(5.0);
        dto.setCategory(FoodCategory.FRUITS.name());
        return dto;
    }

    private User createUser(Long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setRole(UserRole.USER);
        return user;
    }

    private UserDTO createUserDTO(Long id, String username, UserRole role) {
        UserDTO dto = new UserDTO();
        dto.setId(id);
        dto.setUsername(username);
        dto.setEmail(username + "@example.com");
        dto.setRole(role);
        return dto;
    }

    private UserProfileDTO createUserProfileDTO(String username) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUsername(username);
        dto.setEmail(username + "@example.com");
        dto.setGender(Gender.MALE);
        dto.setActivityLevel(ActivityLevel.MODERATELY_ACTIVE);
        dto.setAge(30);
        dto.setHeight(180.0);
        dto.setWeight(75.0);
        return dto;
    }
}