package com.example.onlineshopback.model;

public enum CategoryEnum {
    ELECTRONICS("Electronics"),
    CLOTHING("Clothing"),
    BOOKS("Books"),
    HOME("Home"),
    BEAUTY("Beauty"),
    TOYS("Toys"),
    SPORTS("Sports"),
    FOOD("Food"),
    FURNITURE("Furniture");

    private final String displayName;

    CategoryEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
