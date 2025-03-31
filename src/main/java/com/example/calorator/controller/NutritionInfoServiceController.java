package com.example.calorator.controller;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.service.NutritionInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calorator")
@RequiredArgsConstructor
public class NutritionInfoServiceController {

    private final NutritionInfoService nutritionInfoService;

    @GetMapping("/food-items")
    public ResponseEntity<PagedModel<FoodItemDTO>> getAllFoodItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        try {
            Page<FoodItemDTO> foodItems = nutritionInfoService.getAllFoodItems(page, size, sort, direction);

            PagedModel<FoodItemDTO> pagedModel = PagedModel.of(
                    foodItems.getContent(),
                    new PagedModel.PageMetadata(
                            foodItems.getSize(),
                            foodItems.getNumber(),
                            foodItems.getTotalElements(),
                            foodItems.getTotalPages()
                    )
            );

            pagedModel.add(WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(NutritionInfoServiceController.class)
                                    .getAllFoodItems(page, size, sort, direction))
                    .withSelfRel());

            return ResponseEntity.ok(pagedModel);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(PagedModel.empty());
        }
    }

    @PostMapping("/food-items")
    public ResponseEntity<FoodItemDTO> createFoodItem(@RequestBody FoodItemDTO foodItemDTO) {
        FoodItemDTO createdFoodItem = nutritionInfoService.createFoodItem(foodItemDTO);
        return ResponseEntity.ok(createdFoodItem);
    }

    @PutMapping("/food-items/{id}")
    public ResponseEntity<FoodItemDTO> updateFoodItem(@PathVariable Long id, @RequestBody FoodItemDTO foodItemDTO) {
        FoodItemDTO updatedFoodItem = nutritionInfoService.updateFoodItem(id, foodItemDTO);
        return ResponseEntity.ok(updatedFoodItem);
    }
}
