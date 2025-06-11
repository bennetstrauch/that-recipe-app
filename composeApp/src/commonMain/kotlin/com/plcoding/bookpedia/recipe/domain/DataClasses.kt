package com.plcoding.bookpedia.recipe.domain

// --- Primary Domain Models ---

data class RecipeHeader(
    val id: String,
    val title: String,
    val category: Category,
    val imageUrl: String?,
    val defaultPrepTimeMinutes: Int?,
    val isFavorite: Boolean = false,
)

data class RecipeVersion(
    val id: String,
    val recipeHeaderId: String,
    val versionName: String,
    val versionCommentary: String?,
    val ingredients: List<Ingredient>,
    val directions: List<InstructionStep>,
    val overridePrepTimeMinutes: Int?,
    val createdAt: Long
)

// A convenient class for when you have a header and a selected version together
data class FullRecipe(
    val header: RecipeHeader,
    val selectedVersion: RecipeVersion
)

// --- Supporting Domain Models ---

data class Category(
    val id: String,
    val name: String
)

data class Ingredient(
    val id: String,
    val customDisplayName: String,
    val standardIngredient: StandardIngredient,
    val quantity: Double,
    val measureUnit: MeasureUnit
)

data class StandardIngredient(
    val id: String,
    val name: String,
    // Optional: Density in grams per milliliter (g/ml).
    // This allows for future conversion between volume and weight units.
    // Example values: Water is ~1.0, All-Purpose Flour is ~0.53
    val density: Double? = null
)

data class InstructionStep(
    val id: String,
    val description: String,
    val timerInfo: TimerInfo? = null
)

data class MeasureUnit(
    val id: String,
    val name: String,
    val abbreviation: String?,
    val measurementType: MeasurementType,
    val conversionFactorToSystemBase: Double,
    val isSystemUnit: Boolean
)

enum class MeasurementType {
    VOLUME, WEIGHT, PIECE
}

data class TimerInfo(
    val durationSeconds: Long,
)