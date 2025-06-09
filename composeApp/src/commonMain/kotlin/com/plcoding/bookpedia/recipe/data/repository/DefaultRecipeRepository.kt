package com.plcoding.bookpedia.recipe.data.repository

import com.plcoding.bookpedia.recipe.domain.Ingredient
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.domain.MeasurementType
import com.plcoding.bookpedia.recipe.domain.MeasureUnit
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import com.plcoding.bookpedia.recipe.domain.RecipeVersion

private val dummyRecipeHeader123 = RecipeHeader(
    id = "123",
    title = "Spaghetti Carbonara",
    categoryId = "italian",
    imageUrl = "https://www.example.com/spaghetti.jpg",
    defaultPrepTimeMinutes = 30
)
class DefaultRecipeRepository : RecipeRepository {

    private val dummyMeasureUnitGram = MeasureUnit(
        id = "gram",
        name = "Gram",
        abbreviation = "g",
        measurementType = MeasurementType.WEIGHT,
        conversionFactorToSystemBase = 1.0,
        isSystemUnit = true
    )

    private val dummyMeasureUnitPiece = MeasureUnit(
        id = "piece",
        name = "Piece",
        abbreviation = "pc",
        measurementType = MeasurementType.PIECE,
        conversionFactorToSystemBase = 1.0,
        isSystemUnit = true
    )

    private val dummyRecipeVersionClassic = RecipeVersion(
        id = "version_classic_123",
        recipeHeaderId = "123",
        versionName = "Classic",
        versionCommentary = "The traditional way.",
        ingredients = listOf(
            Ingredient(id = "ing_pasta_1", name = "Spaghetti", quantity = 200.0, measureUnitId = "gram"),
            Ingredient(id = "ing_guanciale_1", name = "Guanciale", quantity = 100.0, measureUnitId = "gram"),
            Ingredient(id = "ing_egg_1", name = "Egg Yolks", quantity = 3.0, measureUnitId = "piece"),
            Ingredient(id = "ing_pecorino_1", name = "Pecorino Romano", quantity = 50.0, measureUnitId = "gram"),
            Ingredient(id = "ing_pepper_1", name = "Black Pepper", quantity = 1.0, measureUnitId = "piece") // Assuming piece for a pinch or to taste
        ),
        directions = listOf(
            InstructionStep(id = "step_1_123", description = "Cook spaghetti according to package directions."),
            InstructionStep(id = "step_2_123", description = "Fry guanciale until crispy."),
            InstructionStep(id = "step_3_123", description = "Whisk egg yolks and Pecorino Romano."),
            InstructionStep(id = "step_4_123", description = "Combine all ingredients and mix well. Season with black pepper.")
        ),
        overridePrepTimeMinutes = null,
        createdAt = 1754778600000
    )

    private val allDummyVersions = listOf(dummyRecipeVersionClassic)

    override suspend fun getRecipeHeaderById(id: String): RecipeHeader? {
        return if (id == "123") dummyRecipeHeader123 else null
    }

    override suspend fun getVersionsForRecipe(headerId: String): List<RecipeVersion> {
        return if (headerId == "123") {
            allDummyVersions.filter { it.recipeHeaderId == headerId }
        } else {
            emptyList()
        }
    }
}