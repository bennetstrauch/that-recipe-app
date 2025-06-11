package com.plcoding.bookpedia.recipe.data.repository

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.recipe.data.database.RecipeDao
import com.plcoding.bookpedia.recipe.data.toDomain
import com.plcoding.bookpedia.recipe.domain.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// # errorhandling properly
class DefaultRecipeRepository(
    private val dao: RecipeDao
) : RecipeRepository {

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
            // 1. Fetch all versions and their related flat entities
            val versionsWithDetails = dao.getRecipeVersionsWithDetails(headerId)
            if (versionsWithDetails.isEmpty()) {
                return Result.Success(emptyList())
            }

            // 2. Collect all unique IDs needed for the rich domain objects
            val allIngredientEntities = versionsWithDetails.flatMap { it.ingredients }
            val standardIngredientIds = allIngredientEntities.map { it.standardIngredientId }.distinct()
            val measureUnitIds = allIngredientEntities.map { it.measureUnitId }.distinct()

            // 3. Fetch all related domain objects in efficient batch queries
            val standardIngredientsMap = dao.getStandardIngredientsByIds(standardIngredientIds)
                .associateBy { it.id }
                .mapValues { (_, entity) -> entity.toDomain() }
            val measureUnitsMap = dao.getMeasureUnitsByIds(measureUnitIds)
                .associateBy { it.id }
                .mapValues { (_, entity) -> entity.toDomain() }

            // 4. Assemble the final rich domain objects with explicit steps
            val domainVersions = mutableListOf<RecipeVersion>()
            for (versionWithDetails in versionsWithDetails) {
                // Map ingredients for this specific version
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
                    } else {
                        null // Skip ingredient if its dependencies are missing
                    }
                }.sortedBy { it.id } // Restore order if needed

                // Map the instruction steps for this version
                val mappedDirections = versionWithDetails.directions
                    .map { it.toDomain() }
                    .sortedBy { it.id } // Restore order if needed

                // Construct the final domain object directly, instead of using a mapper
                val domainVersion = RecipeVersion(
                    id = versionWithDetails.version.id,
                    recipeHeaderId = versionWithDetails.version.recipeHeaderId,
                    versionName = versionWithDetails.version.versionName,
                    versionCommentary = versionWithDetails.version.versionCommentary,
                    ingredients = mappedIngredients,
                    directions = mappedDirections,
                    overridePrepTimeMinutes = versionWithDetails.version.overridePrepTimeMinutes,
                    createdAt = versionWithDetails.version.createdAt
                )
                domainVersions.add(domainVersion)
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
