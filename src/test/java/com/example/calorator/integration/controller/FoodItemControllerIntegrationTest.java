package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.service.FoodItemService;
import jakarta.persistence.EntityNotFoundException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FoodItemControllerIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodItemService foodItemService;

    @Test
    @WithAnonymousUser
    void getFoodItemsPage_Unauthenticated_ShouldRedirectToLogin() throws Exception {
        mockMvc.perform(get("/food-items"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoodItemsPage_Authenticated_ShouldReturnFoodItemsView() throws Exception {

        List<FoodItemDTO> foodItems = Arrays.asList(
                createTestFoodItem(1L, "Apple"),
                createTestFoodItem(2L, "Chicken Breast")
        );
        Page<FoodItemDTO> foodItemPage = new PageImpl<>(foodItems);

        when(foodItemService.getAllFoodItems(any(Pageable.class))).thenReturn(foodItemPage);

        mockMvc.perform(get("/food-items")
                        .param("page", "0")
                        .param("size", "10")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-items/list"))
                .andExpect(model().attributeExists("foodItems"))
                .andExpect(model().attributeExists("currentPage"))
                .andExpect(model().attributeExists("totalPages"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoodItemDetailsPage_ExistingItem_ShouldReturnDetailsView() throws Exception {
        FoodItemDTO foodItem = createTestFoodItem(1L, "Apple");

        when(foodItemService.getFoodItemById(1L)).thenReturn(foodItem);

        mockMvc.perform(get("/food-items/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-items/view"))
                .andExpect(model().attributeExists("foodItem"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getFoodItemDetailsPage_NonExistingItem_ShouldRedirectToFoodItems() throws Exception {
        when(foodItemService.getFoodItemById(anyLong())).thenThrow(new EntityNotFoundException("Food item not found"));

        mockMvc.perform(get("/food-items/999")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/food-items"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getAddFoodItemPage_ShouldReturnAddFoodItemView() throws Exception {
        mockMvc.perform(get("/food-items/create")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("food-items/create"))
                .andExpect(model().attributeExists("foodItem"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("servingUnits"));
    }

    @Test
    @WithMockUser(username = "testuser")
    void postAddFoodItem_ValidData_ShouldRedirectToFoodItems() throws Exception {
        when(foodItemService.createFoodItem(any(FoodItemDTO.class))).thenReturn(createTestFoodItem(1L, "New Food"));

        mockMvc.perform(post("/food-items/create")
                        .param("name", "New Food")
                        .param("calories", "100.0")
                        .param("protein", "5.0")
                        .param("carbs", "10.0")
                        .param("fat", "2.0")
                        .param("servingSize", "100.0")
                        .param("servingUnit", "GRAM")
                        .param("category", "FRUIT")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/food-items"));
    }

    private FoodItemDTO createTestFoodItem(Long id, String name) {
        FoodItemDTO foodItem = new FoodItemDTO();
        foodItem.setId(id);
        foodItem.setName(name);
        foodItem.setCalories(100.0);
        foodItem.setProtein(5.0);
        foodItem.setCarbs(10.0);
        foodItem.setFat(2.0);
        foodItem.setServingSize(100.0);
        foodItem.setServingUnit("GRAM");
        foodItem.setCategory("PROTEIN");
        return foodItem;
    }
}