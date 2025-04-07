package com.example.calorator.integration.service;

import com.example.calorator.integration.BaseIntegrationTest;
import com.example.calorator.model.RestResponsePage;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.service.NutritionInfoService;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
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

    // Mock RestClient components
    private RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;
    private RestClient.RequestHeadersSpec<?> requestHeadersSpec;
    private RestClient.RequestBodySpec requestBodySpec;
    private RestClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        // Create test food items
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

        // Setup mock RestClient components
        requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        requestBodySpec = mock(RestClient.RequestBodySpec.class);
        responseSpec = mock(RestClient.ResponseSpec.class);
    }

    @Test
    void getAllFoodItems_ShouldReturnFoodItems() throws Exception {
        // Create a mock JSON response that matches the structure expected by RestResponsePage
        String jsonResponse = """
                {
                  "content": [
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
                    },
                    {
                      "id": 2,
                      "name": "Chicken Breast",
                      "calories": 165.0,
                      "protein": 31.0,
                      "carbs": 0.0,
                      "fat": 3.6,
                      "servingSize": 100.0,
                      "servingUnit": "GRAM",
                      "category": "PROTEIN"
                    }
                  ],
                  "pageable": {
                    "sort": {
                      "empty": false,
                      "sorted": true,
                      "unsorted": false
                    },
                    "offset": 0,
                    "pageNumber": 0,
                    "pageSize": 10,
                    "paged": true,
                    "unpaged": false
                  },
                  "last": true,
                  "totalPages": 1,
                  "totalElements": 2,
                  "size": 10,
                  "number": 0,
                  "sort": {
                    "empty": false,
                    "sorted": true,
                    "unsorted": false
                  },
                  "first": true,
                  "numberOfElements": 2,
                  "empty": false
                }
                """;

        // Setup mock behavior with proper doReturn/when syntax for chained methods
        doReturn(requestHeadersUriSpec).when(nutritionInfoRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doReturn(jsonResponse).when(responseSpec).body(String.class);

        // Create the expected JavaType for deserialization
        JavaType javaType = mock(JavaType.class);
        TypeFactory typeFactory = mock(TypeFactory.class);

        // Setup ObjectMapper mock behavior
        doReturn(typeFactory).when(objectMapper).getTypeFactory();
        doReturn(javaType).when(typeFactory).constructParametricType(eq(RestResponsePage.class), eq(FoodItemDTO.class));

        // Create a mock page to return from the objectMapper
        RestResponsePage<FoodItemDTO> mockPage = new RestResponsePage<>(
                Arrays.asList(
                        createFoodItemDTO(1L, "Apple", 52.0, "FRUITS"),
                        createFoodItemDTO(2L, "Chicken Breast", 165.0, "PROTEIN")
                ),
                PageRequest.of(0, 10),
                2
        );

        doReturn(mockPage).when(objectMapper).readValue(eq(jsonResponse), eq(javaType));

        // Execute service method
        Page<FoodItemDTO> result = nutritionInfoService.getAllFoodItems(0, 10, "name", "asc");

        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Apple");
        assertThat(result.getContent().get(1).getName()).isEqualTo("Chicken Breast");

        // Verify interactions
        verify(nutritionInfoRestClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(String.class);
    }

    // Helper method to create food item DTOs
    private FoodItemDTO createFoodItemDTO(Long id, String name, Double calories, String category) {
        FoodItemDTO dto = new FoodItemDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setCalories(calories);
        dto.setCategory(category);
        return dto;
    }

    @Test
    void getAllFoodItems_WhenExceptionOccurs_ShouldReturnEmptyPage() {
        // Setup mock behavior to throw exception
        doReturn(requestHeadersUriSpec).when(nutritionInfoRestClient).get();
        doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(requestHeadersSpec).retrieve();
        doThrow(new RuntimeException("API Error")).when(responseSpec).body(String.class);

        // Execute service method
        Page<FoodItemDTO> result = nutritionInfoService.getAllFoodItems(0, 10, "name", "asc");

        // Verify the result is an empty page
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        // Verify interactions
        verify(nutritionInfoRestClient).get();
        verify(requestHeadersUriSpec).uri(any(Function.class));
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).body(String.class);
    }

    @Test
    void createFoodItem_ShouldReturnCreatedFoodItem() {
        // Setup mock behavior
        when(nutritionInfoRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(FoodItemDTO.class)).thenReturn(testFoodItem1);

        // Execute service method
        FoodItemDTO result = nutritionInfoService.createFoodItem(testFoodItem1);

        // Verify the result
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Apple");
        assertThat(result.getCalories()).isEqualTo(52.0);

        // Verify interactions
        verify(nutritionInfoRestClient).post();
        verify(requestBodyUriSpec).uri("/api/food-items");
        verify(requestBodySpec).body(testFoodItem1);
        verify(requestBodySpec).retrieve();
        verify(responseSpec, times(2)).onStatus(any(), any());
        verify(responseSpec).body(FoodItemDTO.class);
    }

    @Test
    void createFoodItem_WhenClientError_ShouldThrowException() {
        // Setup mock behavior for client error
        when(nutritionInfoRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        // Configure onStatus to execute the error handler for 4xx errors
        doAnswer(invocation -> {
            throw new RuntimeException("Invalid food item data");
        }).when(responseSpec).onStatus(eq(HttpStatusCode::is4xxClientError), any());

        when(responseSpec.onStatus(eq(HttpStatusCode::is5xxServerError), any())).thenReturn(responseSpec);

        // Execute service method and verify exception
        assertThatThrownBy(() -> nutritionInfoService.createFoodItem(testFoodItem1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Invalid food item data");

        // Verify interactions
        verify(nutritionInfoRestClient).post();
        verify(requestBodyUriSpec).uri("/api/food-items");
        verify(requestBodySpec).body(testFoodItem1);
        verify(requestBodySpec).retrieve();
        verify(responseSpec).onStatus(eq(HttpStatusCode::is4xxClientError), any());
    }

    @Test
    void createFoodItem_WhenServerError_ShouldThrowException() {
        // Setup mock behavior for server error
        when(nutritionInfoRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.onStatus(eq(HttpStatusCode::is4xxClientError), any())).thenReturn(responseSpec);

        // Configure onStatus to execute the error handler for 5xx errors
        doAnswer(invocation -> {
            throw new RuntimeException("Server error");
        }).when(responseSpec).onStatus(eq(HttpStatusCode::is5xxServerError), any());

        // Execute service method and verify exception
        assertThatThrownBy(() -> nutritionInfoService.createFoodItem(testFoodItem1))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Server error");

        // Verify interactions
        verify(nutritionInfoRestClient).post();
        verify(requestBodyUriSpec).uri("/api/food-items");
        verify(requestBodySpec).body(testFoodItem1);
        verify(requestBodySpec).retrieve();
        verify(responseSpec).onStatus(eq(HttpStatusCode::is4xxClientError), any());
        verify(responseSpec).onStatus(eq(HttpStatusCode::is5xxServerError), any());
    }
}