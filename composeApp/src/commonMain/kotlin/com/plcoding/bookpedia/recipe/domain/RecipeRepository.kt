package com.plcoding.bookpedia.recipe.domain

import kotlinx.coroutines.flow.Flow

interface RecipeRepository {

//    RecipeList:
    suspend fun getAllRecipeHeaders(): List<RecipeHeader>

    suspend fun searchRecipes(query: String): List<RecipeHeader>

//    ##really need to remove suspend?
    fun getFavoriteRecipeHeaders(): Flow<List<RecipeHeader>>

//    RecipeDetails:
    suspend fun getRecipeHeaderById(id: String): RecipeHeader?

    suspend fun getVersionsForRecipe(headerId: String): List<RecipeVersion>


    /** additionalthings:
     * Observes the favorite status of a specific recipe.
     * Returns a Flow so the UI can update automatically if the status changes.
     */
    fun isRecipeFavorite(headerId: String): Flow<Boolean>

    /**
     * Marks a recipe as a favorite.
     */
    suspend fun markAsFavorite(headerId: String)

    /**
     * Removes a recipe from favorites.
     */
    suspend fun removeFromFavorites(headerId: String)

}