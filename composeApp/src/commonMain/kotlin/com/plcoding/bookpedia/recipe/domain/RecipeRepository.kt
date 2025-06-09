package com.plcoding.bookpedia.recipe.domain

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.EmptyResult
import com.plcoding.bookpedia.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

    // --- RecipeList Functions ---
    /**
     * Fetches all recipe headers. Can fail if there's a local DB error.
     */
    suspend fun getAllRecipeHeaders(): Result<List<RecipeHeader>, DataError.Local>

    /**
     * Searches for recipes. This could involve local or remote errors in the future.
     */
    suspend fun searchRecipes(query: String): Result<List<RecipeHeader>, DataError>

    /**
     * Observes the list of favorite recipes.
     * A Flow is a stream, so it handles its own errors via the .catch operator.
     * It does not need to be wrapped in a Result.
     */
    fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>>

    // --- RecipeDetails Functions ---
    /**
     * Retrieves a single recipe header.
     * The success type is RecipeHeader? to handle the "not found" case gracefully.
     */
    suspend fun getRecipeHeaderById(id: String): Result<RecipeHeader?, DataError.Local>

    /**
     * Retrieves all versions for a recipe. Can fail if the headerId is invalid or DB fails.
     */
    suspend fun getVersionsForRecipe(headerId: String): Result<List<RecipeVersion>, DataError.Local>

    // --- Additional Functions ---
    /**
     * Observes the favorite status of a specific recipe.
     * As a Flow, it handles its own errors.
     */
    fun isRecipeFavorite(headerId: String): Flow<Boolean>

    /**
     * Marks a recipe as a favorite. Returns an EmptyResult to indicate success or failure of the action.
     */
    suspend fun markAsFavorite(headerId: String): EmptyResult<DataError.Local>

    /**
     * Removes a recipe from favorites. Returns an EmptyResult to indicate success or failure.
     */
    suspend fun removeFromFavorites(headerId: String): EmptyResult<DataError.Local>
}