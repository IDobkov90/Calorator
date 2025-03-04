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

}
