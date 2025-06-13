@file:OptIn(ExperimentalUuidApi::class)

package com.plcoding.bookpedia.recipe.data.repository

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import com.plcoding.bookpedia.recipe.data.database.RecipeDao
import com.plcoding.bookpedia.recipe.data.database.RecipeHeaderTransferEntity
import com.plcoding.bookpedia.recipe.data.database.RecipeVersionTransferEntity
import com.plcoding.bookpedia.recipe.data.toDomain
import com.plcoding.bookpedia.recipe.data.toEntity
import com.plcoding.bookpedia.recipe.domain.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// # errorhandling properly
class DefaultRecipeRepository(
    private val dao: RecipeDao
) : RecipeRepository {

    override suspend fun createNewRecipe(header: RecipeHeader, version: RecipeVersion): Result<String, DataError> {
        return try {
            // For a brand new recipe, generate all new IDs.
            val newHeaderId = Uuid.random().toString()
            val newVersion = version.copy(
                id = Uuid.random().toString(),
                recipeHeaderId = newHeaderId,
                // Also create new IDs for all child elements to ensure uniqueness.
                ingredients = version.ingredients.map { it.copy(id = Uuid.random().toString()) },
                directions = version.directions.map { it.copy(id = Uuid.random().toString()) }
            )
            val finalHeader = header.copy(id = newHeaderId)

            saveRecipeInternal(finalHeader, newVersion)
            Result.Success(newHeaderId)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun saveRecipeChanges(header: RecipeHeader, version: RecipeVersion): EmptyResult<DataError.Local> {
        return try {
            // This function saves changes to an existing header and an existing version.
            // The IDs are preserved, so @Upsert will perform an UPDATE.
            saveRecipeInternal(header, version)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun saveAsNewVersion(header: RecipeHeader, newVersionData: RecipeVersion): EmptyResult<DataError.Local> {
        return try {
            // --- THIS IS THE CORRECTED LOGIC ---
            // 1. Create a truly new version object with a new unique ID.
            // 2. Also create new unique IDs for all its ingredients and steps.
            val finalVersion = newVersionData.copy(
                id = Uuid.random().toString(),
                // Ensure it's linked to the correct header
                recipeHeaderId = header.id,
                ingredients = newVersionData.ingredients.map { it.copy(id = Uuid.random().toString()) },
                directions = newVersionData.directions.map { it.copy(id = Uuid.random().toString()) }
            )

            // Call the internal helper to save the (potentially updated) header
            // and the brand new version. Because the version and its contents have new IDs,
            // @Upsert will perform an INSERT for them, leaving the original version untouched.
            saveRecipeInternal(header, finalVersion)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun insertStandardIngredient(ingredient: StandardIngredient) : EmptyResult<DataError.Local> {
//        ###useai to match
        return try {
            dao.upsertStandardIngredient(ingredient.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    /**
     * Private helper to perform the actual database transaction.
     * It saves the header, a version, and its related ingredients/steps.
     */
    private suspend fun saveRecipeInternal(header: RecipeHeader, version: RecipeVersion) {
        val headerEntity = header.toEntity()
        val versionEntity = version.toEntity() // This no longer holds the lists
        println("ingredientspassed#: " + version.ingredients.toString())
        val ingredientEntities = version.ingredients.mapIndexed { index, ingredient ->
            ingredient.toEntity(recipeVersionId = version.id, order = index)
        }
        println("ingrediententities#: " + ingredientEntities.toString())
        val stepEntities = version.directions.mapIndexed { index, step ->
            step.toEntity(recipeVersionId = version.id, order = index)
        }

        // This transactional DAO method will handle updating/inserting everything correctly.
        dao.saveFullVersion(headerEntity, versionEntity, ingredientEntities, stepEntities)
    }


    override fun getAllRecipeHeaders(): Flow<Result<List<RecipeHeader>, DataError.Local>> {
        return dao.getRecipeHeadersWithCategory()
            .map { list -> Result.Success(list.map { it.toDomain() }) }
            .onStart<Result<List<RecipeHeader>, DataError.Local>> { /* Do nothing, just set the type, so .catch knows it can emit Result of Type Error */ }
            .catch { e ->
                e.printStackTrace()
                emit(Result.Error(DataError.Local.UNKNOWN))
            }
    }

    override fun searchRecipes(query: String): Flow<Result<List<RecipeHeader>, DataError>> {
        return dao.searchRecipeHeadersWithCategory(query)
            .map { list ->
                Result.Success(list.map { it.toDomain() })
            }
            .onStart<Result<List<RecipeHeader>, DataError>> { /* Do nothing */ }
            .catch { e ->
                e.printStackTrace()
                emit(Result.Error(DataError.Local.UNKNOWN))
            }
    }

    override fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>> {
        return dao.getFavoriteRecipeHeadersWithCategory()
            .map { list -> list.map { it.toDomain() } }
    }

    override fun getRecipeHeaderById(id: String): Flow<Result<RecipeHeader?, DataError.Local>> {
        return dao.getRecipeHeaderWithCategoryById(id)
            .map<RecipeHeaderTransferEntity?, Result<RecipeHeader?, DataError.Local>> { headerWithCategory ->
                Result.Success(headerWithCategory?.toDomain())
            }
            .catch { e ->
                e.printStackTrace()
                emit(Result.Error(DataError.Local.UNKNOWN))
            }
    }

        @OptIn(ExperimentalCoroutinesApi::class)
        override fun getVersionsForRecipe(headerId: String): Flow<Result<List<RecipeVersion>, DataError.Local>> {
            return dao.getRecipeVersionsWithDetails(headerId)
                .mapLatest { versionsWithDetails ->
                    // The .mapLatest operator allows us to perform a suspend function call
                    // to assemble our rich domain objects.
                    try {
                        val domainVersions = mapVersionsWithDetailsToDomain(versionsWithDetails)
                        Result.Success(domainVersions)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Result.Error(DataError.Local.UNKNOWN)
                    }
                }
                .catch { e ->
                    // This will catch any errors from the initial database query itself.
                    e.printStackTrace()
                    emit(Result.Error(DataError.Local.UNKNOWN))
                }
        }

        suspend fun mapVersionsWithDetailsToDomain(
            versionsWithDetails: List<RecipeVersionTransferEntity>
        ): List<RecipeVersion> {
            if (versionsWithDetails.isEmpty()) {
                return emptyList()
            }

            // 1. Collect all unique IDs needed for the rich domain objects
            val allIngredientEntities = versionsWithDetails.flatMap { it.ingredients }
            val standardIngredientIds =
                allIngredientEntities.map { it.standardIngredientId }.distinct()
            val measureUnitIds = allIngredientEntities.map { it.measureUnitId }.distinct()

            // 2. Fetch all related domain objects in efficient batch queries
            val standardIngredientsMap = dao.getStandardIngredientsByIds(standardIngredientIds)
                .associateBy { it.id }
                .mapValues { (_, entity) -> entity.toDomain() }
            val measureUnitsMap = dao.getMeasureUnitsByIds(measureUnitIds)
                .associateBy { it.id }
                .mapValues { (_, entity) -> entity.toDomain() }

            // 3. Assemble the final rich domain objects
            return versionsWithDetails.map { versionWithDetails ->
                val mappedIngredients =
                    versionWithDetails.ingredients.mapNotNull { ingredientEntity ->
                        val standardIngredient =
                            standardIngredientsMap[ingredientEntity.standardIngredientId]
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
                    }.sortedBy { it.id }

                val mappedDirections = versionWithDetails.directions
                    .sortedBy { it.itemOrder }
                    .map { it.toDomain() }

                // Construct the final domain object
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
        }


    override suspend fun getAllMeasureUnits(): Result<List<MeasureUnit>, DataError.Local> {
        return try {
            val measureUnits = dao.getAllMeasureUnits().map{ it.toDomain() }
            Result.Success(measureUnits)
        }
//        ## remove this boilerplate through wrapper
        catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }

    }

    override suspend fun searchStandardIngredients(query: String): Result<List<StandardIngredient>, DataError> {
        return try {
            val entities = dao.searchStandardIngredients(query)
            Result.Success(entities.map { it.toDomain() })
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
