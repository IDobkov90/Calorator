package com.example.calorator.integration.controller;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.service.FoodItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FoodItemRestControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FoodItemService foodItemService;

    @Autowired
    private ObjectMapper objectMapper;

    private FoodItemDTO testFoodItem1;
    private FoodItemDTO testFoodItem2;
    private List<FoodItemDTO> testFoodItems;

    @BeforeEach
    void setUp() {

        testFoodItem1 = createTestFoodItem(1L, "Apple", FoodCategory.FRUITS);
        testFoodItem2 = createTestFoodItem(2L, "Chicken Breast", FoodCategory.PROTEIN);
        testFoodItems = Arrays.asList(testFoodItem1, testFoodItem2);
    }

    private FoodItemDTO createTestFoodItem(Long id, String name, FoodCategory category) {
        FoodItemDTO foodItem = new FoodItemDTO();
        foodItem.setId(id);
        foodItem.setName(name);
        foodItem.setCalories(100.0);
        foodItem.setProtein(5.0);
        foodItem.setCarbs(10.0);
        foodItem.setFat(2.0);
        foodItem.setServingSize(100.0);
        foodItem.setServingUnit(ServingUnit.GRAM.name());
        foodItem.setCategory(category.name());
        return foodItem;
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllFoodItems_ShouldReturnPagedResponse() throws Exception {

        Page<FoodItemDTO> foodItemPage = new PageImpl<>(
                testFoodItems,
                Pageable.ofSize(10).withPage(0),
                testFoodItems.size()
        );
        when(foodItemService.getAllFoodItems(any(Pageable.class))).thenReturn(foodItemPage);

        mockMvc.perform(get("/api/food-items")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortBy", "name")
                        .param("direction", "asc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.foodItemDTOList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.foodItemDTOList[0].name", is("Apple")))
                .andExpect(jsonPath("$._embedded.foodItemDTOList[1].name", is("Chicken Breast")))
                .andExpect(jsonPath("$.page.size", is(10)))
                .andExpect(jsonPath("$.page.totalElements", is(2)));

        verify(foodItemService).getAllFoodItems(any(Pageable.class));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFoodItemById_WithValidId_ShouldReturnFoodItem() throws Exception {

        when(foodItemService.getFoodItemById(1L)).thenReturn(testFoodItem1);

        mockMvc.perform(get("/api/food-items/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Apple")))
                .andExpect(jsonPath("$.category", is("FRUITS")))
                .andExpect(jsonPath("$.calories", is(100.0)));

        verify(foodItemService).getFoodItemById(1L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFoodItemById_WithInvalidId_ShouldReturnNotFound() throws Exception {

        when(foodItemService.getFoodItemById(999L)).thenThrow(new RuntimeException("Food item not found"));

        mockMvc.perform(get("/api/food-items/999"))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(foodItemService).getFoodItemById(999L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFoodItemsByCategory_WithValidCategory_ShouldReturnFoodItems() throws Exception {

        when(foodItemService.getFoodItemsByCategory(FoodCategory.FRUITS)).thenReturn(Collections.singletonList(testFoodItem1));

        mockMvc.perform(get("/api/food-items/category/FRUITS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Apple")))
                .andExpect(jsonPath("$[0].category", is("FRUITS")));

        verify(foodItemService).getFoodItemsByCategory(FoodCategory.FRUITS);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getFoodItemsByCategory_WithInvalidCategory_ShouldReturnBadRequest() throws Exception {

        mockMvc.perform(get("/api/food-items/category/INVALID_CATEGORY"))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(foodItemService, never()).getFoodItemsByCategory(any());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCategories_ShouldReturnAllCategories() throws Exception {

        mockMvc.perform(get("/api/food-items/categories"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(FoodCategory.values().length)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder(
                        Arrays.stream(FoodCategory.values())
                                .map(FoodCategory::name)
                                .toArray())))
                .andExpect(jsonPath("$[*].displayName", containsInAnyOrder(
                        Arrays.stream(FoodCategory.values())
                                .map(FoodCategory::getDisplayName)
                                .toArray())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFoodItem_WithValidData_ShouldReturnCreatedFoodItem() throws Exception {

        when(foodItemService.createFoodItem(any(FoodItemDTO.class))).thenReturn(testFoodItem1);

        mockMvc.perform(post("/api/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testFoodItem1))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Apple")))
                .andExpect(jsonPath("$.category", is("FRUITS")));

        verify(foodItemService).createFoodItem(any(FoodItemDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createFoodItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {

        FoodItemDTO invalidFoodItem = new FoodItemDTO();

        mockMvc.perform(post("/api/food-items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidFoodItem))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(foodItemService, never()).createFoodItem(any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFoodItem_WithValidData_ShouldReturnUpdatedFoodItem() throws Exception {

        FoodItemDTO updatedFoodItem = createTestFoodItem(1L, "Green Apple", FoodCategory.FRUITS);
        when(foodItemService.updateFoodItem(any(FoodItemDTO.class))).thenReturn(updatedFoodItem);

        mockMvc.perform(put("/api/food-items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFoodItem))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Green Apple")))
                .andExpect(jsonPath("$.category", is("FRUITS")));

        verify(foodItemService).updateFoodItem(any(FoodItemDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateFoodItem_WithNonExistingId_ShouldReturnNotFound() throws Exception {

        FoodItemDTO nonExistingFoodItem = createTestFoodItem(999L, "Non-existing", FoodCategory.OTHER);
        when(foodItemService.updateFoodItem(any(FoodItemDTO.class)))
                .thenThrow(new RuntimeException("Food item not found"));

        mockMvc.perform(put("/api/food-items/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistingFoodItem))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(foodItemService).updateFoodItem(any(FoodItemDTO.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFoodItem_WithValidId_ShouldReturnNoContent() throws Exception {

        mockMvc.perform(delete("/api/food-items/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(foodItemService).deleteFoodItem(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteFoodItem_WithInvalidId_ShouldReturnInternalServerError() throws Exception {

        doThrow(new RuntimeException("Food item not found")).when(foodItemService).deleteFoodItem(999L);

        mockMvc.perform(delete("/api/food-items/999")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError());

        verify(foodItemService).deleteFoodItem(999L);
    }
}