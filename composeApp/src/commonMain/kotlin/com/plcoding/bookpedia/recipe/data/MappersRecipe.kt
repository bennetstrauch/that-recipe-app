// In data/Mappers.kt

package com.plcoding.bookpedia.recipe.data

import com.plcoding.bookpedia.recipe.data.database.*
import com.plcoding.bookpedia.recipe.domain.*

// --- Mappers from Database Entity to Domain Model ---

fun RecipeHeaderTransferEntity.toDomain(): RecipeHeader {
    return RecipeHeader(
        id = header.id,
        title = header.title,
        category = category?.toDomain() ?: Category(id = "uncategorized_id", name = "Uncategorized"),
        imageUrl = header.imageUrl,
        defaultPrepTimeMinutes = header.defaultPrepTimeMinutes,
        isFavorite = header.isFavorite
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
        measurementType = try {
            MeasurementType.valueOf(measurementType)
        } catch(e: IllegalArgumentException) {
            MeasurementType.PIECE // Safe fallback
        },
        conversionFactorToSystemBase = conversionFactorToSystemBase,
        isSystemUnit = isSystemUnit
    )
}

// --- Mappers from Domain Model to Database Entity ---
// These are used when saving data to the database.

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

fun RecipeVersion.toEntity(): RecipeVersionEntity {
    // This mapper no longer handles the lists, as they are separate entities.
    return RecipeVersionEntity(
        id = id,
        recipeHeaderId = recipeHeaderId,
        versionName = versionName,
        versionCommentary = versionCommentary,
        overridePrepTimeMinutes = overridePrepTimeMinutes,
        createdAt = createdAt
    )
}

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

// --- NEWLY ADDED MAPPERS ---
fun Category.toEntity(): CategoryEntity = CategoryEntity(id, name)
fun StandardIngredient.toEntity(): StandardIngredientEntity = StandardIngredientEntity(id, name, density)
fun MeasureUnit.toEntity(): MeasureUnitEntity {
    return MeasureUnitEntity(
        id, name, abbreviation, measurementType.name, conversionFactorToSystemBase, isSystemUnit
    )
}