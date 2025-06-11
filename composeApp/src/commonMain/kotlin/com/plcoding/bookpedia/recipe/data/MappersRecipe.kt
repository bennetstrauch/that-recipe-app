// In data/Mappers.kt

package com.plcoding.bookpedia.recipe.data

import com.plcoding.bookpedia.recipe.data.database.*
import com.plcoding.bookpedia.recipe.domain.*

// --- Mappers from Database to Domain ---

fun RecipeHeaderWithCategory.toDomain(): RecipeHeader {
    return RecipeHeader(
        id = header.id,
        title = header.title,
        category = category?.toDomain() ?: Category(id = "uncategorized_id", name = "Uncategorized"),
        imageUrl = header.imageUrl,
        defaultPrepTimeMinutes = header.defaultPrepTimeMinutes,
        isFavorite = header.isFavorite
    )
}

// Helper mapper for a single IngredientEntity.
// Note: This CANNOT create the full domain object on its own.
// The repository will use this and combine it with other DAO calls.
fun IngredientEntity.toPartialDomain(): Ingredient {
    // This is a "partial" mapping. We temporarily create placeholder objects
    // for the related items. The repository will fill these in.
    return Ingredient(
        id = id,
        customDisplayName = customDisplayName,
        quantity = quantity,
        standardIngredient = StandardIngredient(id = standardIngredientId, name = ""), // Placeholder
        measureUnit = MeasureUnit(id = measureUnitId, name = "", abbreviation = null, measurementType = MeasurementType.PIECE, conversionFactorToSystemBase = 1.0, isSystemUnit = false) // Placeholder
    )
}

fun InstructionStepEntity.toDomain(): InstructionStep {
    return InstructionStep(
        id = id,
        description = description,
        timerInfo = timerDurationSeconds?.let { TimerInfo(it) }
    )
}

fun CategoryEntity.toDomain(): Category = Category(id, name)

fun StandardIngredientEntity.toDomain(): StandardIngredient = StandardIngredient(id, name, density)

fun MeasureUnitEntity.toDomain(): MeasureUnit {
    return MeasureUnit(
        id = id,
        name = name,
        abbreviation = abbreviation,
        measurementType = MeasurementType.valueOf(measurementType),
        conversionFactorToSystemBase = conversionFactorToSystemBase,
        isSystemUnit = isSystemUnit
    )
}

// --- Mappers from Domain to Database ---

fun RecipeHeader.toEntity(): RecipeHeaderEntity {
    return RecipeHeaderEntity(
        id = id,
        title = title,
        categoryId = category.id,
        imageUrl = imageUrl,
        defaultPrepTimeMinutes = defaultPrepTimeMinutes,
        isFavorite = isFavorite
    )
}

// When saving, we need to convert the domain objects back to flat entities.
fun Ingredient.toEntity(recipeVersionId: String, order: Int): IngredientEntity {
    return IngredientEntity(
        id = id,
        recipeVersionId = recipeVersionId,
        customDisplayName = customDisplayName,
        standardIngredientId = standardIngredient.id,
        quantity = quantity,
        measureUnitId = measureUnit.id,
        itemOrder = order
    )
}

fun InstructionStep.toEntity(recipeVersionId: String, order: Int): InstructionStepEntity {
    return InstructionStepEntity(
        id = id,
        recipeVersionId = recipeVersionId,
        description = description,
        timerDurationSeconds = timerInfo?.durationSeconds,
        itemOrder = order
    )
}