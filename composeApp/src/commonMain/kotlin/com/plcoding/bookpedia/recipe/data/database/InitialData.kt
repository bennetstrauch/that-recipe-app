package com.plcoding.bookpedia.recipe.data.database

// This file contains entity objects used to seed the database on its first creation.

object InitialData {

    val categories = listOf(
        CategoryEntity("cat1", "Desserts"),
        CategoryEntity("cat2", "Main Courses"),
        CategoryEntity("cat3", "Soups"),
        CategoryEntity("cat4", "Salads")
    )

    val measureUnits = listOf(
        MeasureUnitEntity("unit_g", "Gram", "g", "WEIGHT", 1.0, true),
        MeasureUnitEntity("unit_kg", "Kilogram", "kg", "WEIGHT", 1000.0, true),
        MeasureUnitEntity("unit_ml", "Milliliter", "ml", "VOLUME", 1.0, true),
        MeasureUnitEntity("unit_l", "Liter", "l", "VOLUME", 1000.0, true),
        MeasureUnitEntity("unit_pcs", "Piece", "pcs", "PIECE", 1.0, true),
        MeasureUnitEntity("unit_tsp", "Teaspoon", "tsp", "VOLUME", 4.92, true),
        MeasureUnitEntity("unit_tbsp", "Tablespoon", "tbsp", "VOLUME", 14.79, true)
    )

    val standardIngredients = listOf(
        StandardIngredientEntity("std_flour", "All-Purpose Flour", 0.53),
        StandardIngredientEntity("std_chicken", "Chicken Breast", null),
        StandardIngredientEntity("std_sugar", "White Sugar", 0.85),
        StandardIngredientEntity("std_butter", "Butter", 0.91),
        StandardIngredientEntity("std_apple", "Apple", null),
        StandardIngredientEntity("std_chickpeas", "Chickpeas", null),
        StandardIngredientEntity("std_coconut_milk", "Coconut Milk", 1.02),
        StandardIngredientEntity("std_curry_powder", "Curry Powder", 0.5)
    )

    // --- Recipe 1: Apple Pie ---
    val applePieHeader = RecipeHeaderEntity("header_pie", "Classic Apple Pie", "cat1", null, 90, true)
    val applePieVersion = RecipeVersionEntity("version_pie_1", "header_pie", "Original", "Grandma's recipe", 90, 1749585531936)
    val applePieIngredients = listOf(
        IngredientEntity("ing_pie_1", "version_pie_1", "All-purpose Flour", "std_flour", 300.0, "unit_g", 1),
        IngredientEntity("ing_pie_2", "version_pie_1", "Cold Butter", "std_butter", 150.0, "unit_g", 2),
        IngredientEntity("ing_pie_3", "version_pie_1", "Apples", "std_apple", 6.0, "unit_pcs", 3)
    )
    val applePieDirections = listOf(
        InstructionStepEntity("step_pie_1", "version_pie_1", "Mix flour and butter.", null, 1),
        InstructionStepEntity("step_pie_2", "version_pie_1", "Bake for 45 minutes.", 2700, 2)
    )

    // --- Recipe 2: Chicken Curry ---
    val chickenCurryHeader = RecipeHeaderEntity("header_curry", "Spicy Chicken Curry", "cat2", null, 45, false)
    val chickenCurryVersion = RecipeVersionEntity("version_curry_1", "header_curry", "Original", "Quick and easy!", 40, 1749525531936)
    val chickenCurryIngredients = listOf(
        IngredientEntity("ing_curry_1", "version_curry_1", "Chicken Breast", "std_chicken", 500.0, "unit_g", 1),
        IngredientEntity("ing_curry_2", "version_curry_1", "Coconut Milk", "std_coconut_milk", 400.0, "unit_ml", 2),
        IngredientEntity("ing_curry_3", "version_curry_1", "Curry Powder", "std_curry_powder", 2.0, "unit_tsp", 3)
    )
    val chickenCurryDirections = listOf(
        InstructionStepEntity("step_curry_1", "version_curry_1", "Fry chicken until golden.", null, 1),
        InstructionStepEntity("step_curry_2", "version_curry_1", "Add coconut milk and simmer for 20 minutes.", 1200, 2)
    )
}