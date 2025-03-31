package com.example.calorator.service;

import com.example.calorator.model.RestResponsePage;
import com.example.calorator.model.dto.FoodItemDTO;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class NutritionInfoService {

    private static final Logger logger = LoggerFactory.getLogger(NutritionInfoService.class);

    private final RestClient nutritionInfoRestClient;
    private final ObjectMapper objectMapper;

    public Page<FoodItemDTO> getAllFoodItems(int page, int size, String sort, String direction) {
        try {
            String responseBody = nutritionInfoRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/food-items")
                            .queryParam("page", page)
                            .queryParam("size", size)
                            .queryParam("sort", sort)
                            .queryParam("direction", direction)
                            .build())
                    .retrieve()
                    .body(String.class);

            logger.info("Response from external service: {}", responseBody);

            JavaType type = objectMapper.getTypeFactory().constructParametricType(RestResponsePage.class, FoodItemDTO.class);
            RestResponsePage<FoodItemDTO> resultPage = objectMapper.readValue(responseBody, type);

            logger.info("Deserialized page: {}", resultPage);

            return resultPage;
        } catch (Exception e) {
            logger.error("Error fetching food items from external service", e);
            return Page.empty(PageRequest.of(page, size));
        }
    }

    public FoodItemDTO createFoodItem(FoodItemDTO foodItemDTO) {
        return nutritionInfoRestClient.post()
                .uri("/api/food-items")
                .body(foodItemDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new RuntimeException("Invalid food item data");
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new RuntimeException("Server error");
                        })
                .body(FoodItemDTO.class);
    }

    public FoodItemDTO updateFoodItem(Long id, FoodItemDTO foodItemDTO) {
        return nutritionInfoRestClient.put()
                .uri("/api/food-items/{id}", id)
                .body(foodItemDTO)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        (request, response) -> {
                            throw new RuntimeException("Food item not found or invalid data");
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (request, response) -> {
                            throw new RuntimeException("Server error");
                        })
                .body(FoodItemDTO.class);
    }
}
