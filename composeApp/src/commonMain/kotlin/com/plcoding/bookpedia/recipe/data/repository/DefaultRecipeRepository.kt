@file:OptIn(ExperimentalUuidApi::class)

package com.plcoding.bookpedia.recipe.data.repository

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.recipe.data.database.RecipeDao
import com.plcoding.bookpedia.recipe.data.toDomain
import com.plcoding.bookpedia.recipe.data.toEntity
import com.plcoding.bookpedia.recipe.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// # errorhandling properly
class DefaultRecipeRepository(
    private val dao: RecipeDao
) : RecipeRepository {

    override suspend fun createNewRecipe(header: RecipeHeader, version: RecipeVersion): Result<String, DataError> {
        return try {
            val newHeaderId = Uuid.random().toString()
            val newVersionId = Uuid.random().toString()

            val finalHeader = header.copy(id = newHeaderId)
            val finalVersion = version.copy(id = newVersionId, recipeHeaderId = newHeaderId)

            saveRecipeInternal(finalHeader, finalVersion)
            Result.Success(newHeaderId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun saveRecipeChanges(header: RecipeHeader, version: RecipeVersion): EmptyResult<DataError.Local> {
        return try {
            saveRecipeInternal(header, version)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun saveAsNewVersion(header: RecipeHeader, newVersion: RecipeVersion): EmptyResult<DataError.Local> {
        return try {
            // --- FIXED --- Use named argument `id` for clarity and correctness
            val finalVersion = newVersion.copy(id = Uuid.random().toString())

            saveRecipeInternal(header, finalVersion)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    private suspend fun saveRecipeInternal(header: RecipeHeader, version: RecipeVersion) {
        val headerEntity = header.toEntity()
        val versionWithCorrectHeaderId = version.copy(recipeHeaderId = header.id)

        val versionEntity = versionWithCorrectHeaderId.toEntity()
        val ingredientEntities = versionWithCorrectHeaderId.ingredients.mapIndexed { index, ingredient ->
            ingredient.toEntity(recipeVersionId = versionWithCorrectHeaderId.id, order = index)
        }
        val stepEntities = versionWithCorrectHeaderId.directions.mapIndexed { index, step ->
            step.toEntity(recipeVersionId = versionWithCorrectHeaderId.id, order = index)
        }
        dao.saveFullVersion(headerEntity, versionEntity, ingredientEntities, stepEntities)
    }


    override suspend fun getAllRecipeHeaders(): Result<List<RecipeHeader>, DataError.Local> {
        return try {
            val headersWithCategory = dao.getRecipeHeadersWithCategory()
            Result.Success(headersWithCategory.map { it.toDomain() })
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun searchRecipes(query: String): Result<List<RecipeHeader>, DataError> {
        return try {
            // In a real app, this would be a DAO query with a LIKE clause.
            // For now, we filter the full list.
            val allHeadersResult = getAllRecipeHeaders()
            if (allHeadersResult is Result.Error) return allHeadersResult

            val filtered = (allHeadersResult as Result.Success).data.filter {
                it.title.contains(query, ignoreCase = true)
            }
            Result.Success(filtered)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>> {
        return dao.getFavoriteRecipeHeadersWithCategory()
            .map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRecipeHeaderById(id: String): Result<RecipeHeader?, DataError.Local> {
        return try {
            val headerWithCategory = dao.getRecipeHeaderWithCategoryById(id)
            Result.Success(headerWithCategory?.toDomain())
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

//    ##refactor
override suspend fun getVersionsForRecipe(headerId: String): Result<List<RecipeVersion>, DataError.Local> {
    return try {
        val versionsWithDetails = dao.getRecipeVersionsWithDetails(headerId)
        if (versionsWithDetails.isEmpty()) {
            return Result.Success(emptyList())
        }

        val allIngredientEntities = versionsWithDetails.flatMap { it.ingredients }
        val standardIngredientIds = allIngredientEntities.map { it.standardIngredientId }.distinct()
        val measureUnitIds = allIngredientEntities.map { it.measureUnitId }.distinct()

        val standardIngredientsMap = dao.getStandardIngredientsByIds(standardIngredientIds)
            .associateBy { it.id }
            .mapValues { (_, entity) -> entity.toDomain() }
        val measureUnitsMap = dao.getMeasureUnitsByIds(measureUnitIds)
            .associateBy { it.id }
            .mapValues { (_, entity) -> entity.toDomain() }

        val domainVersions = versionsWithDetails.map { versionWithDetails ->
            val mappedIngredients = versionWithDetails.ingredients.mapNotNull { ingredientEntity ->
                val standardIngredient = standardIngredientsMap[ingredientEntity.standardIngredientId]
                val measureUnit = measureUnitsMap[ingredientEntity.measureUnitId]

                if (standardIngredient != null && measureUnit != null) {
                    Ingredient(
                        id = ingredientEntity.id,
                        customDisplayName = ingredientEntity.customDisplayName,
                        quantity = ingredientEntity.quantity,
                        standardIngredient = standardIngredient,
                        measureUnit = measureUnit
                    )
                } else { null }
            }.sortedBy { ingredient ->
                // --- FIXED --- Find the original entity to sort by its `itemOrder`
                allIngredientEntities.find { it.id == ingredient.id }?.itemOrder
            }

            val mappedDirections = versionWithDetails.directions
                .sortedBy { it.itemOrder } // --- FIXED --- Ensure order is maintained
                .map { it.toDomain() }

            RecipeVersion(
                id = versionWithDetails.version.id,
                recipeHeaderId = versionWithDetails.version.recipeHeaderId,
                versionName = versionWithDetails.version.versionName,
                versionCommentary = versionWithDetails.version.versionCommentary,
                ingredients = mappedIngredients,
                directions = mappedDirections,
                overridePrepTimeMinutes = versionWithDetails.version.overridePrepTimeMinutes,
                createdAt = versionWithDetails.version.createdAt
            )
        }
        Result.Success(domainVersions)
    } catch (e: Exception) {
        e.printStackTrace()
        Result.Error(DataError.Local.UNKNOWN)
    }
}


    override fun isRecipeFavorite(headerId: String): Flow<Boolean> {
        return getFavoriteRecipeHeaders().map { favorites ->
            favorites.any { it.id == headerId }
        }
    }

    override suspend fun markAsFavorite(headerId: String): EmptyResult<DataError.Local> {
        return try {
            dao.markAsFavorite(headerId)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun removeFromFavorites(headerId: String): EmptyResult<DataError.Local> {
        return try {
            dao.removeFromFavorites(headerId)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
