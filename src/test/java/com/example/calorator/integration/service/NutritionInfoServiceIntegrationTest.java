package com.example.calorator.integration.service;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.service.NutritionInfoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class NutritionInfoServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private NutritionInfoService nutritionInfoService;

    @MockitoBean
    private RestClient nutritionInfoRestClient;

    @Autowired
    private ObjectMapper objectMapper;

    private FoodItemDTO testFoodItem1;
    private FoodItemDTO testFoodItem2;
    private List<FoodItemDTO> foodItems;

    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;
    private RestClient.RequestBodySpec requestBodySpec;
    private RestClient.ResponseSpec responseSpec;

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

        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        requestBodySpec = mock(RestClient.RequestBodySpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);
    }

    @Test
    void getAllFoodItems_ShouldReturnFoodItems() {

        doReturn(requestHeadersUriSpec).when(nutritionInfoRestClient).get();
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(String.class)).thenReturn("""
                {
                    "_embedded": {
                        "foodItemDTOList": [
                            {
                                "id": 1,
                                "name": "Apple",
                                "calories": 52.0,
                                "protein": 0.3,
                                "carbs": 14.0,
                                "fat": 0.2,
                                "servingSize": 100.0,
                                "servingUnit": "GRAM",
                                "category": "FRUITS"
                            }
                        ]
                    },
                    "page": {
                        "size": 10,
                        "totalElements": 1,
                        "totalPages": 1,
                        "number": 0
                    }
                }
                """);


        Page<FoodItemDTO> result = nutritionInfoService.getAllFoodItems(0, 10, "name", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);

        FoodItemDTO foodItem = result.getContent().getFirst();
        assertThat(foodItem.getId()).isEqualTo(1L);
        assertThat(foodItem.getName()).isEqualTo("Apple");
        assertThat(foodItem.getCalories()).isEqualTo(52.0);
        assertThat(foodItem.getProtein()).isEqualTo(0.3);
        assertThat(foodItem.getCarbs()).isEqualTo(14.0);
        assertThat(foodItem.getFat()).isEqualTo(0.2);
        assertThat(foodItem.getServingSize()).isEqualTo(100.0);
        assertThat(foodItem.getServingUnit()).isEqualTo("GRAM");
        assertThat(foodItem.getCategory()).isEqualTo("FRUITS");


        verify(nutritionInfoRestClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(String.class);
    }

    @Test
    void getAllFoodItems_WhenExceptionOccurs_ShouldReturnEmptyPage() {

        doReturn(requestHeadersUriSpec).when(nutritionInfoRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doThrow(new RuntimeException("API Error")).when(responseSpec).body(String.class);

        Page<FoodItemDTO> result = nutritionInfoService.getAllFoodItems(0, 10, "name", "asc");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(nutritionInfoRestClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(String.class);
    }

    @Test
    void createFoodItem_ShouldReturnCreatedFoodItem() {

        when(nutritionInfoRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(FoodItemDTO.class)).thenReturn(testFoodItem1);

        FoodItemDTO result = nutritionInfoService.createFoodItem(testFoodItem1);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Apple");
        assertThat(result.getCalories()).isEqualTo(52.0);

        verify(nutritionInfoRestClient).post();
        verify(requestBodyUriSpec).uri("/api/food-items");
        verify(requestBodySpec).body(testFoodItem1);
        verify(requestBodySpec).retrieve();
        verify(responseSpec, times(2)).onStatus(any(), any());
        verify(responseSpec).body(FoodItemDTO.class);
    }

    @Test
    void createFoodItem_WhenClientError_ShouldThrowException() {

        when(nutritionInfoRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        when(requestBodySpec.retrieve()).thenThrow(new RuntimeException("Invalid food item data"));

        assertThatThrownBy(() -> nutritionInfoService.createFoodItem(testFoodItem1))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid food item data");

        verify(nutritionInfoRestClient).post();
        verify(requestBodyUriSpec).uri("/api/food-items");
        verify(requestBodySpec).body(testFoodItem1);
    }

    @Test
    void createFoodItem_WhenServerError_ShouldThrowException() {

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(nutritionInfoRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        when(responseSpec.body(eq(FoodItemDTO.class))).thenThrow(
                new RuntimeException("Server error")
        );

        assertThatThrownBy(() -> nutritionInfoService.createFoodItem(testFoodItem1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Server error");

        verify(nutritionInfoRestClient).post();
        verify(requestBodyUriSpec).uri("/api/food-items");
        verify(requestBodySpec).body(testFoodItem1);
    }
}