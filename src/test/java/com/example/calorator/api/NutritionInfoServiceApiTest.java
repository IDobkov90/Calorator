package com.example.calorator.api;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.service.NutritionInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(username = "testuser", roles = {"USER"})
class NutritionInfoServiceApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NutritionInfoService nutritionInfoService;

    @Autowired
    private ObjectMapper objectMapper;

    private FoodItemDTO testFoodItem1;
    private FoodItemDTO testFoodItem2;
    private List<FoodItemDTO> foodItems;
    private Page<FoodItemDTO> foodItemPage;

    @BeforeEach
    void setUp() {

        testFoodItem1 = new FoodItemDTO();
        testFoodItem1.setId(1L);
        testFoodItem1.setName("Apple");
        testFoodItem1.setCalories(52.0);
        testFoodItem1.setProtein(0.3);
        testFoodItem1.setCarbs(14.0);
        testFoodItem1.setFat(0.2);
        testFoodItem1.setServingSize(100.0);
        testFoodItem1.setServingUnit(ServingUnit.GRAM.name());
        testFoodItem1.setCategory(FoodCategory.FRUITS.name());

        testFoodItem2 = new FoodItemDTO();
        testFoodItem2.setId(2L);
        testFoodItem2.setName("Chicken Breast");
        testFoodItem2.setCalories(165.0);
        testFoodItem2.setProtein(31.0);
        testFoodItem2.setCarbs(0.0);
        testFoodItem2.setFat(3.6);
        testFoodItem2.setServingSize(100.0);
        testFoodItem2.setServingUnit(ServingUnit.GRAM.name());
        testFoodItem2.setCategory(FoodCategory.PROTEIN.name());

        foodItems = Arrays.asList(testFoodItem1, testFoodItem2);
        foodItemPage = new PageImpl<>(foodItems, PageRequest.of(0, 10), 2);
    }

    @Test
    void getAllFoodItems_ShouldReturnFoodItems() throws Exception {
        Page<FoodItemDTO> foodItemPage = new PageImpl<>(Collections.singletonList(testFoodItem1));

        when(nutritionInfoService.getAllFoodItems(eq(0), eq(10), eq("id"), eq("asc")))
                .thenReturn(foodItemPage);

        mockMvc.perform(get("/api/calorator/food-items")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id")
                        .param("direction", "asc")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.foodItemDTOList[0].name", is("Apple")));

        verify(nutritionInfoService).getAllFoodItems(eq(0), eq(10), eq("id"), eq("asc"));
    }

    @Test
    void getAllFoodItems_WhenServiceThrowsException_ShouldReturnEmptyPage() throws Exception {

        when(nutritionInfoService.getAllFoodItems(anyInt(), anyInt(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/api/calorator/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    void createFoodItem_ShouldCreateAndReturnFoodItem() throws Exception {
        when(nutritionInfoService.createFoodItem(any(FoodItemDTO.class))).thenReturn(testFoodItem1);

        mockMvc.perform(post("/api/calorator/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodItem1))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Apple")));

        verify(nutritionInfoService).createFoodItem(ArgumentMatchers.any(FoodItemDTO.class));
    }

    @Test
    void createFoodItem_WhenServiceThrowsException_ShouldPropagateError() throws Exception {

        when(nutritionInfoService.createFoodItem(ArgumentMatchers.any(FoodItemDTO.class)))
                .thenThrow(new RuntimeException("Food item already exists"));

        mockMvc.perform(post("/api/calorator/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodItem1))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void updateFoodItem_ShouldReturnUpdatedFoodItem() throws Exception {

        when(nutritionInfoService.updateFoodItem(eq(1L), ArgumentMatchers.any(FoodItemDTO.class)))
                .thenReturn(testFoodItem1);

        mockMvc.perform(put("/api/calorator/food-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodItem1))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Apple")))
                .andExpect(jsonPath("$.calories", is(52.0)))
                .andExpect(jsonPath("$.protein", is(0.3)))
                .andExpect(jsonPath("$.carbs", is(14.0)))
                .andExpect(jsonPath("$.fat", is(0.2)));

        verify(nutritionInfoService).updateFoodItem(eq(1L), ArgumentMatchers.any(FoodItemDTO.class));
    }

    @Test
    void updateFoodItem_WhenServiceThrowsException_ShouldPropagateError() throws Exception {

        when(nutritionInfoService.updateFoodItem(eq(1L), ArgumentMatchers.any(FoodItemDTO.class)))
                .thenThrow(new RuntimeException("Food item not found"));

        mockMvc.perform(put("/api/calorator/food-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodItem1))
                        .with(csrf()))
                .andExpect(status().isInternalServerError());
    }
}