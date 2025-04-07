package com.example.calorator.integration.repository;

import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import com.example.calorator.repository.FoodItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FoodItemRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FoodItemRepository foodItemRepository;

    private FoodItem testFoodItem1;
    private FoodItem testFoodItem2;
    private FoodItem testFoodItem3;

    @BeforeEach
    void setUp() {

        foodItemRepository.deleteAll();
        entityManager.flush();
        entityManager.clear();

        testFoodItem1 = new FoodItem();
        testFoodItem1.setName("Apple");
        testFoodItem1.setCalories(52.0);
        testFoodItem1.setProtein(0.3);
        testFoodItem1.setCarbs(14.0);
        testFoodItem1.setFat(0.2);
        testFoodItem1.setServingSize(100.0);
        testFoodItem1.setServingUnit(ServingUnit.GRAM);
        testFoodItem1.setCategory(FoodCategory.FRUITS);
        entityManager.persist(testFoodItem1);

        testFoodItem2 = new FoodItem();
        testFoodItem2.setName("Chicken Breast");
        testFoodItem2.setCalories(165.0);
        testFoodItem2.setProtein(31.0);
        testFoodItem2.setCarbs(0.0);
        testFoodItem2.setFat(3.6);
        testFoodItem2.setServingSize(100.0);
        testFoodItem2.setServingUnit(ServingUnit.GRAM);
        testFoodItem2.setCategory(FoodCategory.PROTEIN);
        entityManager.persist(testFoodItem2);

        testFoodItem3 = new FoodItem();
        testFoodItem3.setName("Brown Rice");
        testFoodItem3.setCalories(112.0);
        testFoodItem3.setProtein(2.6);
        testFoodItem3.setCarbs(23.5);
        testFoodItem3.setFat(0.9);
        testFoodItem3.setServingSize(100.0);
        testFoodItem3.setServingUnit(ServingUnit.GRAM);
        testFoodItem3.setCategory(FoodCategory.GRAINS);
        entityManager.persist(testFoodItem3);

        entityManager.flush();
    }

    @Test
    void findById_ShouldReturnFoodItem() {

        Optional<FoodItem> result = foodItemRepository.findById(testFoodItem1.getId());

        assertTrue(result.isPresent());
        assertEquals(testFoodItem1.getName(), result.get().getName());
        assertEquals(testFoodItem1.getCalories(), result.get().getCalories());
    }

    @Test
    void findByCategory_ShouldReturnMatchingFoodItems() {

        List<FoodItem> result = foodItemRepository.findByCategory(FoodCategory.FRUITS);

        assertThat(result).hasSize(1);
        assertEquals(testFoodItem1.getName(), result.getFirst().getName());
    }

    @Test
    void findByCategoryWithPagination_ShouldReturnPagedResults() {

        Page<FoodItem> result = foodItemRepository.findByCategory(
                FoodCategory.GRAINS, PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertEquals(testFoodItem3.getName(), result.getContent().getFirst().getName());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void findByNameContainingIgnoreCase_ShouldReturnMatchingFoodItems() {

        List<FoodItem> result1 = foodItemRepository.findByNameContainingIgnoreCase("apple");

        assertThat(result1).hasSize(1);
        assertEquals(testFoodItem1.getName(), result1.getFirst().getName());

        List<FoodItem> result2 = foodItemRepository.findByNameContainingIgnoreCase("ChIcKeN");

        assertThat(result2).hasSize(1);
        assertEquals(testFoodItem2.getName(), result2.getFirst().getName());

        List<FoodItem> result3 = foodItemRepository.findByNameContainingIgnoreCase("ow");

        assertThat(result3).hasSize(1);
        assertEquals(testFoodItem3.getName(), result3.getFirst().getName());
    }

    @Test
    void existsByNameIgnoreCase_ShouldReturnTrueForExistingName() {

        boolean exists1 = foodItemRepository.existsByNameIgnoreCase("Apple");

        assertTrue(exists1);

        boolean exists2 = foodItemRepository.existsByNameIgnoreCase("chicken breast");

        assertTrue(exists2);

        boolean exists3 = foodItemRepository.existsByNameIgnoreCase("Banana");

        assertFalse(exists3);
    }

    @Test
    void findAllByOrderByCreatedAtDesc_ShouldReturnItemsInDescendingOrder() {

        Pageable pageable = PageRequest.of(0, 10);
        Page<FoodItem> result = foodItemRepository.findAllByOrderByCreatedAtDesc(pageable);

        assertThat(result.getContent()).hasSize(3);
        assertEquals(3, result.getTotalElements());

        List<String> foodNames = result.getContent().stream()
                .map(FoodItem::getName)
                .toList();

        assertThat(foodNames).contains(
                testFoodItem1.getName(),
                testFoodItem2.getName(),
                testFoodItem3.getName()
        );
    }

    @Test
    void saveAndFlush_ShouldPersistNewFoodItem() {

        FoodItem newFoodItem = new FoodItem();
        newFoodItem.setName("Banana");
        newFoodItem.setCalories(89.0);
        newFoodItem.setProtein(1.1);
        newFoodItem.setCarbs(22.8);
        newFoodItem.setFat(0.3);
        newFoodItem.setServingSize(100.0);
        newFoodItem.setServingUnit(ServingUnit.GRAM);
        newFoodItem.setCategory(FoodCategory.FRUITS);

        FoodItem savedItem = foodItemRepository.saveAndFlush(newFoodItem);

        assertNotNull(savedItem.getId());

        Optional<FoodItem> retrievedItem = foodItemRepository.findById(savedItem.getId());
        assertTrue(retrievedItem.isPresent());
        assertEquals("Banana", retrievedItem.get().getName());
    }

    @Test
    void delete_ShouldRemoveFoodItem() {

        Long idToDelete = testFoodItem1.getId();

        foodItemRepository.deleteById(idToDelete);
        entityManager.flush();

        Optional<FoodItem> result = foodItemRepository.findById(idToDelete);
        assertFalse(result.isPresent());

        assertEquals(2, foodItemRepository.count());
    }
}