package com.plcoding.bookpedia.recipe.data

import com.plcoding.bookpedia.recipe.domain.*

// Dummy Categories
val category1 = Category(id = "cat1", name = "Desserts")
val category2 = Category(id = "cat2", name = "Main Courses")

// Dummy Measure Units
val unitGrams = MeasureUnit("unit1", "Gram", "g", MeasurementType.WEIGHT, 1.0, true)
val unitMl = MeasureUnit("unit2", "Milliliter", "ml", MeasurementType.VOLUME, 1.0, true)
val unitPiece = MeasureUnit("unit3", "Piece", "pcs", MeasurementType.PIECE, 1.0, true)
val unitTsp = MeasureUnit("unit4", "Teaspoon", "tsp", MeasurementType.VOLUME, 4.92, true)

// --- Recipe 1: Apple Pie ---
val applePieHeader = RecipeHeader(
    id = "header1",
    title = "Classic Apple Pie",
    categoryId = category1.id,
    imageUrl = null, // Can add a URL here
    defaultPrepTimeMinutes = 90,
    isFavorite = true
)

val applePieVersionOriginal = RecipeVersion(
    id = "version1_1",
    recipeHeaderId = applePieHeader.id,
    versionName = "Original",
    versionCommentary = "Grandma's classic recipe. The best!",
    ingredients = listOf(
        Ingredient("ing1", "All-purpose Flour", 300.0, unitGrams.id),
        Ingredient("ing2", "Butter, cold", 150.0, unitGrams.id),
        Ingredient("ing3", "Apples", 6.0, unitPiece.id),
        Ingredient("ing4", "Sugar", 100.0, unitGrams.id)
    ),
    directions = listOf(
        InstructionStep("step1_1", "Mix flour and butter to create the dough."),
        InstructionStep("step1_2", "Press dough into pie form and let it cool for 30 minutes.", timerInfo = TimerInfo(1800)),
        InstructionStep("step1_3", "Peel and slice the apples."),
        InstructionStep("step1_4", "Bake at 180°C for 45 minutes.", timerInfo = TimerInfo(2700))
    ),
    overridePrepTimeMinutes = 90,
    createdAt = 1754778600000 + 20000
)

// --- Recipe 2: Chicken Curry ---
val chickenCurryHeader = RecipeHeader(
    id = "header2",
    title = "Spicy Chicken Curry",
    categoryId = category2.id,
    imageUrl = null,
    defaultPrepTimeMinutes = 45,
    isFavorite = false
)

val chickenCurryVersionOriginal = RecipeVersion(
    id = "version2_1",
    recipeHeaderId = chickenCurryHeader.id,
    versionName = "Original",
    versionCommentary = "A quick and easy weeknight dinner.",
    ingredients = listOf(
        Ingredient("ing5", "Chicken Breast", 500.0, unitGrams.id),
        Ingredient("ing6", "Coconut Milk", 400.0, unitMl.id),
        Ingredient("ing7", "Curry Powder", 2.0, unitTsp.id)
    ),
    directions = listOf(
        InstructionStep("step2_1", "Cut chicken into bite-sized pieces."),
        InstructionStep("step2_2", "Fry chicken until golden brown."),
        InstructionStep("step2_3", "Add coconut milk and curry powder, then simmer for 20 minutes.", timerInfo = TimerInfo(1200))
    ),
    overridePrepTimeMinutes = 40,
    createdAt = 1754778600000 - 100000
)

val chickenCurryVersionVegan = RecipeVersion(
    id = "version2_2",
    recipeHeaderId = chickenCurryHeader.id,
    versionName = "Vegan",
    versionCommentary = "Replaced chicken with chickpeas for a vegan-friendly version.",
    ingredients = listOf(
        Ingredient("ing8", "Chickpeas, canned", 400.0, unitGrams.id),
        Ingredient("ing6", "Coconut Milk", 400.0, unitMl.id),
        Ingredient("ing7", "Curry Powder", 2.0, unitTsp.id)
    ),
    directions = listOf(
        InstructionStep("step2_4", "Sauté chickpeas with spices."),
        InstructionStep("step2_5", "Add coconut milk and simmer for 15 minutes.", timerInfo = TimerInfo(900))
    ),
    overridePrepTimeMinutes = 25,
    createdAt = 1754778600000
)


// --- All Dummy Data ---
val dummyRecipeHeaders = listOf(applePieHeader, chickenCurryHeader)
val dummyRecipeVersions = listOf(applePieVersionOriginal, chickenCurryVersionOriginal, chickenCurryVersionVegan)