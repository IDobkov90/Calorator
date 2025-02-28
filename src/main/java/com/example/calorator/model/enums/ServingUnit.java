package com.example.calorator.model.enums;

public enum ServingUnit {
    GRAM("g"),
    MILLILITER("ml"),
    OUNCE("oz"),
    CUP("cup"),
    TABLESPOON("tbsp"),
    TEASPOON("tsp"),
    PIECE("pc");

    private final String abbreviation;

    ServingUnit(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}
