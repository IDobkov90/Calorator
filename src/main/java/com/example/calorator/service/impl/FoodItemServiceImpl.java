package com.example.calorator.service.impl;

import com.example.calorator.mapper.FoodItemMapper;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.repository.FoodItemRepository;
import com.example.calorator.service.FoodItemService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodItemServiceImpl implements FoodItemService {

    private static final String FOOD_ITEM_NOT_FOUND_MESSAGE = "Food item not found with id: ";

    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper foodItemMapper;

    @Override
    public Page<FoodItemDTO> getAllFoodItems(Pageable pageable) {
        Page<FoodItem> foodItemPage = foodItemRepository.findAll(pageable);
        return foodItemPage.map(foodItemMapper::toDto);
    }

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
                .orElseThrow(() -> new EntityNotFoundException(FOOD_ITEM_NOT_FOUND_MESSAGE + id));
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
        foodItemDTO.setId(null);
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

        FoodItem existingItem = foodItemRepository.findById(foodItemDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException(FOOD_ITEM_NOT_FOUND_MESSAGE + foodItemDTO.getId()));

        validateNutritionalValues(foodItemDTO);

        foodItemMapper.updateEntityFromDto(foodItemDTO, existingItem);

        FoodItem savedItem = foodItemRepository.save(existingItem);

        return foodItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public void deleteFoodItem(Long id) {
        foodItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(FOOD_ITEM_NOT_FOUND_MESSAGE + id));
        foodItemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodItemDTO> getRecentFoodItems(Pageable pageable) {
        Page<FoodItem> foodItemPage = foodItemRepository.findAllByOrderByCreatedAtDesc(pageable);
        return foodItemPage.map(foodItemMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long countFoodItems() {
        return foodItemRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FoodItemDTO> getFoodItemsByCategory(FoodCategory category, Pageable pageable) {
        Page<FoodItem> foodItemPage = foodItemRepository.findByCategory(category, pageable);
        return foodItemPage.map(foodItemMapper::toDto);
    }

    private void validateNutritionalValues(FoodItemDTO dto) {
        double totalGrams = dto.getProtein() + dto.getCarbs() + dto.getFat();
        String servingUnit = dto.getServingUnit();

        double toleranceFactor = 1.05;

        if ("GRAM".equalsIgnoreCase(servingUnit) && totalGrams > dto.getServingSize() * toleranceFactor) {
            throw new IllegalArgumentException("Total macronutrients cannot exceed serving size");
        } else if ("MILLILITER".equalsIgnoreCase(servingUnit) && totalGrams > dto.getServingSize() * toleranceFactor) {
            throw new IllegalArgumentException("Total macronutrients cannot exceed serving size");
        } else if ("OUNCE".equalsIgnoreCase(servingUnit) && totalGrams > dto.getServingSize() * 28.35 * toleranceFactor) {
            throw new IllegalArgumentException("Total macronutrients cannot exceed serving size");
        } else if ("CUP".equalsIgnoreCase(servingUnit) && totalGrams > dto.getServingSize() * 240 * toleranceFactor) {
            throw new IllegalArgumentException("Total macronutrients cannot exceed serving size");
        } else if ("TABLESPOON".equalsIgnoreCase(servingUnit) && totalGrams > dto.getServingSize() * 15 * toleranceFactor) {
            throw new IllegalArgumentException("Total macronutrients cannot exceed serving size");
        } else if ("TEASPOON".equalsIgnoreCase(servingUnit) && totalGrams > dto.getServingSize() * 5 * toleranceFactor) {
            throw new IllegalArgumentException("Total macronutrients cannot exceed serving size");
        }

        double calculatedCalories = (dto.getProtein() * 4) + (dto.getCarbs() * 4) + (dto.getFat() * 9);
        double allowedDeviation = 10;
        if (Math.abs(calculatedCalories - dto.getCalories()) > allowedDeviation) {
            throw new IllegalArgumentException("Calories don't match the macronutrient values");
        }
    }
}
