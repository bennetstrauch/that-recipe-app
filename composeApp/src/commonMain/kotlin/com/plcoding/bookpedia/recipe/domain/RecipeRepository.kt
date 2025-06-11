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

    // --- Read & Favorite Functions ---
    suspend fun getAllRecipeHeaders(): Result<List<RecipeHeader>, DataError.Local>
    suspend fun searchRecipes(query: String): Result<List<RecipeHeader>, DataError>
    fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>>
    suspend fun getRecipeHeaderById(id: String): Result<RecipeHeader?, DataError.Local>
    suspend fun getVersionsForRecipe(headerId: String): Result<List<RecipeVersion>, DataError.Local>
    fun isRecipeFavorite(headerId: String): Flow<Boolean>
    suspend fun markAsFavorite(headerId: String): EmptyResult<DataError.Local>
    suspend fun removeFromFavorites(headerId: String): EmptyResult<DataError.Local>

}