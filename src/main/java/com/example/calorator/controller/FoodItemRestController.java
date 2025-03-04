package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.service.FoodItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/food-items")
@RequiredArgsConstructor
public class FoodItemRestController {

    private final FoodItemService foodItemService;

    @GetMapping
    public ResponseEntity<Page<FoodItemDTO>> getAllFoodItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<FoodItemDTO> foodItems = foodItemService.getAllFoodItems(pageable);
        return ResponseEntity.ok(foodItems);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItemDTO> getFoodItemById(@PathVariable Long id) {
        FoodItemDTO foodItem = foodItemService.getFoodItemById(id);
        return ResponseEntity.ok(foodItem);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<FoodItemDTO>> getFoodItemsByCategory(@PathVariable String category) {
        try {
            FoodCategory foodCategory = FoodCategory.valueOf(category.toUpperCase());
            List<FoodItemDTO> foodItems = foodItemService.getFoodItemsByCategory(foodCategory);
            return ResponseEntity.ok(foodItems);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, String>>> getAllCategories() {
        List<Map<String, String>> categories = Arrays.stream(FoodCategory.values())
                .map(category -> {
                    Map<String, String> categoryMap = new HashMap<>();
                    categoryMap.put("name", category.name());
                    categoryMap.put("displayName", category.getDisplayName());
                    return categoryMap;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<FoodItemDTO> createFoodItem(@Valid @RequestBody FoodItemDTO foodItemDTO) {
        FoodItemDTO createdFoodItem = foodItemService.createFoodItem(foodItemDTO);
        return new ResponseEntity<>(createdFoodItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodItemDTO> updateFoodItem(@PathVariable Long id, @Valid @RequestBody FoodItemDTO foodItemDTO) {
        foodItemDTO.setId(id);
        FoodItemDTO updatedFoodItem = foodItemService.updateFoodItem(foodItemDTO);
        return ResponseEntity.ok(updatedFoodItem);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        foodItemService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }
}
