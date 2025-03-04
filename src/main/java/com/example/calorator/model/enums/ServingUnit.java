package com.example.calorator.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServingUnit {
    GRAM("g"),
    MILLILITER("ml"),
    OUNCE("oz"),
    CUP("cup"),
    TABLESPOON("tbsp"),
    TEASPOON("tsp"),
    PIECE("pc");

    private final String abbreviation;

    public static ServingUnit fromAbbreviation(String abbreviation) {
        if (abbreviation == null) {
            return null;
        }

        for (ServingUnit unit : ServingUnit.values()) {
            if (unit.getAbbreviation().equalsIgnoreCase(abbreviation)) {
                return unit;
            }
        }

        try {
            return ServingUnit.valueOf(abbreviation.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
