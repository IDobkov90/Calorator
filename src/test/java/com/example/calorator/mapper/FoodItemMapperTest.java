package com.example.calorator.mapper;

import com.example.calorator.model.dto.FoodItemDTO;
import com.example.calorator.model.entity.FoodItem;
import com.example.calorator.model.enums.FoodCategory;
import com.example.calorator.model.enums.ServingUnit;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FoodItemMapperTest {

    private final FoodItemMapper mapper = Mappers.getMapper(FoodItemMapper.class);

    @Test
    void toEntity_ShouldMapAllFields() {

        FoodItemDTO dto = new FoodItemDTO();
        dto.setId(1L);
        dto.setName("Apple");
        dto.setCalories(52.0);
        dto.setProtein(0.3);
        dto.setCarbs(14.0);
        dto.setFat(0.2);
        dto.setServingSize(100.0);
        dto.setServingUnit(ServingUnit.GRAM.name());
        dto.setCategory(FoodCategory.FRUITS.name());

        FoodItem entity = mapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Apple");
        assertThat(entity.getCalories()).isEqualTo(52.0);
        assertThat(entity.getProtein()).isEqualTo(0.3);
        assertThat(entity.getCarbs()).isEqualTo(14.0);
        assertThat(entity.getFat()).isEqualTo(0.2);
        assertThat(entity.getServingSize()).isEqualTo(100.0);
        assertThat(entity.getServingUnit()).isEqualTo(ServingUnit.GRAM);
        assertThat(entity.getCategory()).isEqualTo(FoodCategory.FRUITS);
    }

    @Test
    void toDto_ShouldMapAllFields() {

        FoodItem entity = new FoodItem();
        entity.setId(1L);
        entity.setName("Apple");
        entity.setCalories(52.0);
        entity.setProtein(0.3);
        entity.setCarbs(14.0);
        entity.setFat(0.2);
        entity.setServingSize(100.0);
        entity.setServingUnit(ServingUnit.GRAM);
        entity.setCategory(FoodCategory.FRUITS);

        FoodItemDTO dto = mapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Apple");
        assertThat(dto.getCalories()).isEqualTo(52.0);
        assertThat(dto.getProtein()).isEqualTo(0.3);
        assertThat(dto.getCarbs()).isEqualTo(14.0);
        assertThat(dto.getFat()).isEqualTo(0.2);
        assertThat(dto.getServingSize()).isEqualTo(100.0);
        assertThat(dto.getServingUnit()).isEqualTo(ServingUnit.GRAM.name());
        assertThat(dto.getCategory()).isEqualTo(FoodCategory.FRUITS.name());
    }

    @Test
    void updateEntityFromDto_ShouldUpdateAllFields() {

        FoodItemDTO dto = new FoodItemDTO();
        dto.setName("Updated Apple");
        dto.setCalories(55.0);
        dto.setProtein(0.4);
        dto.setCarbs(15.0);
        dto.setFat(0.3);
        dto.setServingSize(120.0);
        dto.setServingUnit(ServingUnit.GRAM.name());
        dto.setCategory(FoodCategory.FRUITS.name());

        FoodItem entity = new FoodItem();
        entity.setId(1L);
        entity.setName("Apple");
        entity.setCalories(52.0);
        entity.setProtein(0.3);
        entity.setCarbs(14.0);
        entity.setFat(0.2);
        entity.setServingSize(100.0);
        entity.setServingUnit(ServingUnit.GRAM);
        entity.setCategory(FoodCategory.FRUITS);

        mapper.updateEntityFromDto(dto, entity);

        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getName()).isEqualTo("Updated Apple");
        assertThat(entity.getCalories()).isEqualTo(55.0);
        assertThat(entity.getProtein()).isEqualTo(0.4);
        assertThat(entity.getCarbs()).isEqualTo(15.0);
        assertThat(entity.getFat()).isEqualTo(0.3);
        assertThat(entity.getServingSize()).isEqualTo(120.0);
        assertThat(entity.getServingUnit()).isEqualTo(ServingUnit.GRAM);
        assertThat(entity.getCategory()).isEqualTo(FoodCategory.FRUITS);
    }

    @Test
    void mapFoodCategory_WithValidCategory_ShouldReturnCorrectEnum() {

        for (FoodCategory category : FoodCategory.values()) {
            assertThat(mapper.mapFoodCategory(category.name())).isEqualTo(category);
        }
    }

    @Test
    void mapFoodCategory_WithNull_ShouldReturnNull() {

        assertThat(mapper.mapFoodCategory(null)).isNull();
    }

    @Test
    void mapFoodCategory_WithInvalidCategory_ShouldReturnNull() {

        assertThrows(IllegalArgumentException.class, () -> mapper.mapFoodCategory("INVALID_CATEGORY"));
    }

    @Test
    void map_FoodCategory_WithValidCategory_ShouldReturnString() {

        for (FoodCategory category : FoodCategory.values()) {
            assertThat(mapper.map(category)).isEqualTo(category.name());
        }
    }

    @Test
    void map_FoodCategory_WithNull_ShouldReturnNull() {

        assertThat(mapper.map((FoodCategory) null)).isNull();
    }

    @Test
    void mapServingUnit_WithValidEnumName_ShouldReturnCorrectEnum() {

        for (ServingUnit unit : ServingUnit.values()) {
            assertThat(mapper.mapServingUnit(unit.name())).isEqualTo(unit);
        }
    }

    @Test
    void mapServingUnit_WithValidAbbreviation_ShouldReturnCorrectEnum() {

        for (ServingUnit unit : ServingUnit.values()) {

            if (unit.getAbbreviation() == null || unit.getAbbreviation().isEmpty()) {
                continue;
            }
            assertThat(mapper.mapServingUnit(unit.getAbbreviation())).isEqualTo(unit);
        }
    }

    @Test
    void mapServingUnit_WithNullOrEmpty_ShouldReturnNull() {

        assertThat(mapper.mapServingUnit(null)).isNull();
        assertThat(mapper.mapServingUnit("")).isNull();
    }

    @Test
    void mapServingUnit_WithInvalidUnit_ShouldThrowException() {

        assertThrows(IllegalArgumentException.class, () -> mapper.mapServingUnit("INVALID_UNIT"));
    }

    @Test
    void map_ServingUnit_WithValidUnit_ShouldReturnString() {

        for (ServingUnit unit : ServingUnit.values()) {
            assertThat(mapper.map(unit)).isEqualTo(unit.name());
        }
    }

    @Test
    void map_ServingUnit_WithNull_ShouldReturnNull() {

        assertThat(mapper.map((ServingUnit) null)).isNull();
    }
}