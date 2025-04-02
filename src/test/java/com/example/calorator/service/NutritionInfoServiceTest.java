package com.example.calorator.service;

import com.example.calorator.model.RestResponsePage;
import com.example.calorator.model.dto.FoodItemDTO;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NutritionInfoServiceTest {

    @Mock
    private RestClient nutritionInfoRestClient;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RestResponsePage<FoodItemDTO> mockPage;

    @InjectMocks
    private NutritionInfoService nutritionInfoService;

    private String mockResponseJson;
    private FoodItemDTO mockFoodItem;

    @BeforeEach
    void setUp() {
        mockResponseJson = "{\"_embedded\":{\"foodItemDTOList\":[{\"id\":1,\"name\":\"Test Food\"}]},\"page\":{\"number\":0,\"size\":10,\"totalElements\":1}}";
        mockFoodItem = new FoodItemDTO();
        mockFoodItem.setId(1L);
        mockFoodItem.setName("Test Food");
    }

    @Test
    void getAllFoodItems_Success() throws Exception {

        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        doReturn(requestHeadersUriSpec).when(nutritionInfoRestClient).get();
        doReturn(requestBodySpec).when(requestHeadersUriSpec).uri(any(Function.class));
        doReturn(responseSpec).when(requestBodySpec).retrieve();
        doReturn(mockResponseJson).when(responseSpec).body(String.class);

        JavaType javaType = mock(JavaType.class);
        TypeFactory typeFactory = mock(TypeFactory.class);

        doReturn(typeFactory).when(objectMapper).getTypeFactory();
        doReturn(javaType).when(typeFactory).constructParametricType(RestResponsePage.class, FoodItemDTO.class);
        doReturn(mockPage).when(objectMapper).readValue(mockResponseJson, javaType);

        Page<FoodItemDTO> result = nutritionInfoService.getAllFoodItems(0, 10, "name", "asc");

        assertNotNull(result);
        assertEquals(mockPage, result);
        verify(nutritionInfoRestClient).get();
        verify(objectMapper).readValue(eq(mockResponseJson), any(JavaType.class));
    }

    @Test
    void getAllFoodItems_Exception_ReturnsEmptyPage() {
        RestClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);

        doReturn(requestHeadersUriSpec).when(nutritionInfoRestClient).get();
        when(requestHeadersUriSpec.uri(any(Function.class))).thenThrow(new RuntimeException("Network error"));

        Page<FoodItemDTO> result = nutritionInfoService.getAllFoodItems(0, 10, "name", "asc");

        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        verify(nutritionInfoRestClient).get();
    }

    @Test
    void createFoodItem_Success() {

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(nutritionInfoRestClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(FoodItemDTO.class)).thenReturn(mockFoodItem);

        FoodItemDTO result = nutritionInfoService.createFoodItem(mockFoodItem);

        assertNotNull(result);
        assertEquals(mockFoodItem, result);
        verify(nutritionInfoRestClient).post();
    }

    @Test
    void updateFoodItem_Success() {

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(nutritionInfoRestClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString(), anyLong())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(FoodItemDTO.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.body(FoodItemDTO.class)).thenReturn(mockFoodItem);

        FoodItemDTO result = nutritionInfoService.updateFoodItem(1L, mockFoodItem);

        assertNotNull(result);
        assertEquals(mockFoodItem, result);
        verify(nutritionInfoRestClient).put();
    }
}