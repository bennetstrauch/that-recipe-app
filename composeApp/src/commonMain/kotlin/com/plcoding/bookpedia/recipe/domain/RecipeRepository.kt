package com.plcoding.bookpedia.recipe.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    // --- Write/Edit Functions ---
    suspend fun createNewRecipe(header: RecipeHeader, version: RecipeVersion): Result<String, DataError>
    suspend fun saveRecipeChanges(header: RecipeHeader, version: RecipeVersion): EmptyResult<DataError.Local>
    suspend fun saveAsNewVersion(header: RecipeHeader, newVersion: RecipeVersion): EmptyResult<DataError.Local>
    suspend fun insertStandardIngredient(ingredient: StandardIngredient) : EmptyResult<DataError.Local>

    // --- Read Functions ---
    fun getAllRecipeHeaders(): Flow<Result<List<RecipeHeader>, DataError.Local>>
    fun searchRecipes(query: String): Flow<Result<List<RecipeHeader>, DataError>>
    fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>>
    fun getRecipeHeaderById(id: String): Flow<Result<RecipeHeader?, DataError.Local>>
    fun getVersionsForRecipe(headerId: String): Flow<Result<List<RecipeVersion>, DataError.Local>>
    suspend fun getAllMeasureUnits(): Result<List<MeasureUnit>, DataError.Local>
    suspend fun searchStandardIngredients(query: String): Result<List<StandardIngredient>, DataError>
    fun getAllCategories(): Flow<Result<List<Category>, DataError.Local>>

    // --- Favorite Functions ---
    fun isRecipeFavorite(headerId: String): Flow<Boolean>
    suspend fun markAsFavorite(headerId: String): EmptyResult<DataError.Local>
    suspend fun removeFromFavorites(headerId: String): EmptyResult<DataError.Local>

}