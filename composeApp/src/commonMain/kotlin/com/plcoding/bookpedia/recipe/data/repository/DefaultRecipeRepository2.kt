//// In data/repository/DefaultRecipeRepository.kt
//
//package com.plcoding.bookpedia.recipe.data.repository
//
//import com.plcoding.bookpedia.core.domain.DataError
//import com.plcoding.bookpedia.core.domain.EmptyResult
//import com.plcoding.bookpedia.core.domain.Result
//import com.plcoding.bookpedia.recipe.data.database.RecipeDao
//import com.plcoding.bookpedia.recipe.data.toDomain
//import com.plcoding.bookpedia.recipe.domain.*
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//
//class DefaultRecipeRepository2(
//    private val dao: RecipeDao
//) : RecipeRepository {
//
//    override suspend fun getAllRecipeHeaders(): Result<List<RecipeHeader>, DataError.Local> {
//        return try {
//            val headersWithCategory = dao.getRecipeHeadersWithCategory()
//            Result.Success(headersWithCategory.map { it.toDomain() })
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(DataError.Local.UNKNOWN)
//        }
//    }
//
//    override suspend fun searchRecipes(query: String): Result<List<RecipeHeader>, DataError> {
//        return try {
//            // In a real app, this would be a DAO query with a LIKE clause
//            val allHeadersResult = getAllRecipeHeaders()
//            if (allHeadersResult is Result.Error) return allHeadersResult
//
//            val filtered = (allHeadersResult as Result.Success).data.filter {
//                it.title.contains(query, ignoreCase = true)
//            }
//            Result.Success(filtered)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(DataError.Local.UNKNOWN)
//        }
//    }
//
//    override fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>> {
//        return dao.getFavoriteRecipeHeadersWithCategory()
//            .map { list -> list.map { it.toDomain() } }
//    }
//
//    override suspend fun getRecipeHeaderById(id: String): Result<RecipeHeader?, DataError.Local> {
//        return try {
//            Result.Success(dao.getRecipeHeaderWithCategoryById(id)?.toDomain())
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(DataError.Local.UNKNOWN)
//        }
//    }
//
//    // --- THIS IS THE FULLY REWRITTEN AND CORRECTED METHOD ---
//    override suspend fun getVersionsForRecipe(headerId: String): Result<List<RecipeVersion>, DataError.Local> {
//        return try {
//            // 1. Fetch all versions and their flat ingredient/direction lists
//            val versionsWithDetails = dao.getRecipeVersionsWithDetails(headerId)
//            if (versionsWithDetails.isEmpty()) {
//                return Result.Success(emptyList())
//            }
//
//            // 2. Collect all unique IDs for related data in one go
//            val allIngredientEntities = versionsWithDetails.flatMap { it.ingredients }
//            val standardIngredientIds = allIngredientEntities.map { it.standardIngredientId }.distinct()
//            val measureUnitIds = allIngredientEntities.map { it.measureUnitId }.distinct()
//
//            // 3. Fetch all related data in two efficient batch queries
//            val standardIngredientsMap = dao.getStandardIngredientsByIds(standardIngredientIds)
//                .associateBy { it.id }
//            val measureUnitsMap = dao.getMeasureUnitsByIds(measureUnitIds)
//                .associateBy { it.id }
//
//            // 4. Now, map everything together in memory
//            val domainVersions = versionsWithDetails.map { versionWithDetails ->
//                // Map ingredients for this specific version
//                val mappedIngredients = versionWithDetails.ingredients.mapNotNull { ingredientEntity ->
//                    // Look up the full objects from our maps
//                    val standardIngredient = standardIngredientsMap[ingredientEntity.standardIngredientId]?.toDomain()
//                    val measureUnit = measureUnitsMap[ingredientEntity.measureUnitId]?.toDomain()
//
//                    // If for some reason a related object wasn't found, skip this ingredient
//                    if (standardIngredient == null || measureUnit == null) {
//                        null
//                    } else {
//                        Ingredient(
//                            id = ingredientEntity.id,
//                            customDisplayName = ingredientEntity.customDisplayName,
//                            quantity = ingredientEntity.quantity,
//                            standardIngredient = standardIngredient,
//                            measureUnit = measureUnit
//                        )
//                    }
//                }.sortedBy { it.id } // Ensure order is maintained if needed
//
//                // Use the helper mapper to assemble the final RecipeVersion domain object
//                versionWithDetails.version.toDomain(
//                    ingredients = mappedIngredients,
//                    directions = versionWithDetails.directions
//                        .sortedBy { it.itemOrder } // Ensure order
//                        .map { it.toDomain() }
//                )
//            }
//            Result.Success(domainVersions)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(DataError.Local.UNKNOWN)
//        }
//    }
//
//
//    override fun isRecipeFavorite(headerId: String): Flow<Boolean> {
//        return getFavoriteRecipeHeaders().map { favorites ->
//            favorites.any { it.id == headerId }
//        }
//    }
//
//    override suspend fun markAsFavorite(headerId: String): EmptyResult<DataError.Local> {
//        return try {
//            dao.markAsFavorite(headerId)
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(DataError.Local.UNKNOWN)
//        }
//    }
//
//    override suspend fun removeFromFavorites(headerId: String): EmptyResult<DataError.Local> {
//        return try {
//            dao.removeFromFavorites(headerId)
//            Result.Success(Unit)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Result.Error(DataError.Local.UNKNOWN)
//        }
//    }
//}