package com.example.calorator.service.impl;

import com.example.calorator.mapper.FoodItemMapper;
import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.repository.FoodItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FoodItemServiceImplTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private FoodItemMapper foodItemMapper;

    @InjectMocks
    private FoodItemServiceImpl foodItemService;

    private FoodItem testFoodItem;
    private FoodItemDTO testFoodItemDTO;

    @BeforeEach
    void setUp() {
        testFoodItem = new FoodItem();
        testFoodItem.setId(1L);
        testFoodItem.setName("Test Food");
        testFoodItem.setCalories(100.0);
        testFoodItem.setProtein(10.0);
        testFoodItem.setCarbs(15.0);
        testFoodItem.setFat(0.0);
        testFoodItem.setServingSize(100.0);
        testFoodItem.setServingUnit(ServingUnit.GRAM);
        testFoodItem.setCategory(FoodCategory.PROTEIN);

        testFoodItemDTO = new FoodItemDTO();
        testFoodItemDTO.setId(1L);
        testFoodItemDTO.setName("Test Food");
        testFoodItemDTO.setCalories(100.0);
        testFoodItemDTO.setProtein(10.0);
        testFoodItemDTO.setCarbs(15.0);
        testFoodItemDTO.setFat(0.0);
        testFoodItemDTO.setServingSize(100.0);
        testFoodItemDTO.setServingUnit("GRAM");
        testFoodItemDTO.setCategory("PROTEIN");
    }

    @Test
    void getAllFoodItems_WithPagination_ShouldReturnPageOfFoodItems() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<FoodItem> foodItemPage = new PageImpl<>(Collections.singletonList(testFoodItem));

        when(foodItemRepository.findAll(pageable)).thenReturn(foodItemPage);
        when(foodItemMapper.toDto(testFoodItem)).thenReturn(testFoodItemDTO);

        Page<FoodItemDTO> result = foodItemService.getAllFoodItems(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testFoodItemDTO.getId(), result.getContent().getFirst().getId());
        verify(foodItemRepository).findAll(pageable);
    }

    @Test
    void getAllFoodItems_ShouldReturnListOfFoodItems() {

        when(foodItemRepository.findAll()).thenReturn(Collections.singletonList(testFoodItem));
        when(foodItemMapper.toDto(testFoodItem)).thenReturn(testFoodItemDTO);

        List<FoodItemDTO> result = foodItemService.getAllFoodItems();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFoodItemDTO.getId(), result.getFirst().getId());
        verify(foodItemRepository).findAll();
    }

    @Test
    void getFoodItemById_ShouldReturnFoodItem() {

        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(testFoodItem));
        when(foodItemMapper.toDto(testFoodItem)).thenReturn(testFoodItemDTO);

        FoodItemDTO result = foodItemService.getFoodItemById(1L);

        assertNotNull(result);
        assertEquals(testFoodItemDTO.getId(), result.getId());
        assertEquals(testFoodItemDTO.getName(), result.getName());
    }

    @Test
    void getFoodItemById_WithNonExistingId_ShouldThrowException() {

        when(foodItemRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> foodItemService.getFoodItemById(999L)
        );
        assertTrue(exception.getMessage().contains("999"));
    }

    @Test
    void getFoodItemsByCategory_ShouldReturnListOfFoodItems() {

        when(foodItemRepository.findByCategory(FoodCategory.PROTEIN))
                .thenReturn(Collections.singletonList(testFoodItem));
        when(foodItemMapper.toDto(testFoodItem)).thenReturn(testFoodItemDTO);

        List<FoodItemDTO> result = foodItemService.getFoodItemsByCategory(FoodCategory.PROTEIN);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testFoodItemDTO.getId(), result.getFirst().getId());
        assertEquals("PROTEIN", result.getFirst().getCategory());
    }

    @Test
    void createFoodItem_ShouldReturnCreatedFoodItem() {

        when(foodItemRepository.existsByNameIgnoreCase(testFoodItemDTO.getName())).thenReturn(false);
        when(foodItemMapper.toEntity(testFoodItemDTO)).thenReturn(testFoodItem);
        when(foodItemRepository.save(testFoodItem)).thenReturn(testFoodItem);
        when(foodItemMapper.toDto(testFoodItem)).thenReturn(testFoodItemDTO);

        FoodItemDTO result = foodItemService.createFoodItem(testFoodItemDTO);

        assertNotNull(result);
        assertEquals(testFoodItemDTO.getId(), result.getId());
        assertEquals(testFoodItemDTO.getName(), result.getName());
        verify(foodItemRepository).save(testFoodItem);
    }

    @Test
    void createFoodItem_WithExistingName_ShouldThrowException() {

        when(foodItemRepository.existsByNameIgnoreCase(testFoodItemDTO.getName())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> foodItemService.createFoodItem(testFoodItemDTO)
        );
        assertEquals("A food item with this name already exists", exception.getMessage());
        verify(foodItemRepository, never()).save(any());
    }

    @Test
    void createFoodItem_WithInvalidMacronutrients_ShouldThrowException() {

        testFoodItemDTO.setProtein(50.0);
        testFoodItemDTO.setCarbs(60.0);
        testFoodItemDTO.setFat(10.0);
        testFoodItemDTO.setServingSize(100.0);

        when(foodItemRepository.existsByNameIgnoreCase(testFoodItemDTO.getName())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> foodItemService.createFoodItem(testFoodItemDTO)
        );
        assertEquals("Total macronutrients cannot exceed serving size", exception.getMessage());
        verify(foodItemRepository, never()).save(any());
    }

    @Test
    void createFoodItem_WithInvalidCalories_ShouldThrowException() {

        testFoodItemDTO.setProtein(10.0);
        testFoodItemDTO.setCarbs(15.0);
        testFoodItemDTO.setFat(0.0);
        testFoodItemDTO.setCalories(200.0);

        when(foodItemRepository.existsByNameIgnoreCase(testFoodItemDTO.getName())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> foodItemService.createFoodItem(testFoodItemDTO)
        );
        assertEquals("Calories don't match the macronutrient values", exception.getMessage());
        verify(foodItemRepository, never()).save(any());
    }

    @Test
    void updateFoodItem_ShouldReturnUpdatedFoodItem() {

        FoodItemDTO updateDTO = new FoodItemDTO();
        updateDTO.setId(1L);
        updateDTO.setName("Updated Food");
        updateDTO.setCalories(100.0);
        updateDTO.setProtein(10.0);
        updateDTO.setCarbs(15.0);
        updateDTO.setFat(0.0);
        updateDTO.setServingSize(100.0);
        updateDTO.setServingUnit("GRAM");
        updateDTO.setCategory("VEGETABLES");

        FoodItem updatedFoodItem = new FoodItem();
        updatedFoodItem.setId(1L);
        updatedFoodItem.setName("Updated Food");
        updatedFoodItem.setCalories(100.0);
        updatedFoodItem.setProtein(10.0);
        updatedFoodItem.setCarbs(15.0);
        updatedFoodItem.setFat(0.0);
        updatedFoodItem.setServingSize(100.0);
        updatedFoodItem.setServingUnit(ServingUnit.GRAM);
        updatedFoodItem.setCategory(FoodCategory.VEGETABLES);

        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(testFoodItem));
        when(foodItemRepository.save(any(FoodItem.class))).thenReturn(updatedFoodItem);
        when(foodItemMapper.toDto(updatedFoodItem)).thenReturn(updateDTO);
        doNothing().when(foodItemMapper).updateEntityFromDto(updateDTO, testFoodItem);

        FoodItemDTO result = foodItemService.updateFoodItem(updateDTO);

        assertNotNull(result);
        assertEquals(updateDTO.getId(), result.getId());
        assertEquals(updateDTO.getName(), result.getName());
        assertEquals(updateDTO.getCategory(), result.getCategory());
        verify(foodItemRepository).save(any(FoodItem.class));
    }

    @Test
    void updateFoodItem_WithNullId_ShouldThrowException() {

        testFoodItemDTO.setId(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> foodItemService.updateFoodItem(testFoodItemDTO)
        );
        assertEquals("ID cannot be null for update operation", exception.getMessage());
        verify(foodItemRepository, never()).save(any());
    }

    @Test
    void deleteFoodItem_ShouldDeleteFoodItem() {

        when(foodItemRepository.findById(1L)).thenReturn(Optional.of(testFoodItem));
        doNothing().when(foodItemRepository).deleteById(1L);

        foodItemService.deleteFoodItem(1L);

        verify(foodItemRepository).findById(1L);
        verify(foodItemRepository).deleteById(1L);
    }
}