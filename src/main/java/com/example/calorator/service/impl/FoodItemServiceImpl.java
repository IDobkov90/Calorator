package com.example.calorator.service.impl;

import com.example.calorator.mapper.FoodItemMapper;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.repository.FoodItemRepository;
import com.example.calorator.service.FoodItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper foodItemMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FoodItemDTO> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(foodItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public FoodItemDTO getFoodItemById(Long id) {
        FoodItem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food item not found with id: " + id));
        return foodItemMapper.toDto(foodItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodItemDTO> getFoodItemsByCategory(FoodCategory category) {
        return foodItemRepository.findByCategory(category).stream()
                .map(foodItemMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public FoodItemDTO createFoodItem(FoodItemDTO foodItemDTO) {
        if (foodItemRepository.existsByNameIgnoreCase(foodItemDTO.getName())) {
            throw new IllegalArgumentException("A food item with this name already exists");
        }
        validateNutritionalValues(foodItemDTO);

        FoodItem foodItem = foodItemMapper.toEntity(foodItemDTO);
        foodItem = foodItemRepository.save(foodItem);
        return foodItemMapper.toDto(foodItem);
    }

    @Override
    @Transactional
    public FoodItemDTO updateFoodItem(FoodItemDTO foodItemDTO) {
        if (foodItemDTO.getId() == null) {
            throw new IllegalArgumentException("ID cannot be null for update operation");
        }

        return foodItemRepository.findById(foodItemDTO.getId())
                .map(existingItem -> {
                    FoodItem updatedItem = foodItemMapper.toEntity(foodItemDTO);
                    return foodItemMapper.toDto(foodItemRepository.save(updatedItem));
                })
                .orElseThrow(() -> new EntityNotFoundException("Food item not found with id: " + foodItemDTO.getId()));
    }

    @Override
    @Transactional
    public void deleteFoodItem(Long id) {
        foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food item not found with id: " + id));
        foodItemRepository.deleteById(id);
    }

    private void validateNutritionalValues(FoodItemDTO dto) {
        double totalGrams = dto.getProtein() + dto.getCarbs() + dto.getFat();
        if (dto.getServingUnit() == ServingUnit.GRAM && totalGrams > dto.getServingSize()) {
            throw new IllegalArgumentException("Total macronutrients cannot exceed serving size");
        }
        double calculatedCalories = (dto.getProtein() * 4) + (dto.getCarbs() * 4) + (dto.getFat() * 9);
        double allowedDeviation = 10;
        if (Math.abs(calculatedCalories - dto.getCalories()) > allowedDeviation) {
            throw new IllegalArgumentException("Calories don't match the macronutrient values");
        }
    }
}
