package com.plcoding.bookpedia.recipe.domain

interface RecipeRepository {

    suspend fun getRecipeHeaderById(id: String): RecipeHeader?
    suspend fun getVersionsForRecipe(headerId: String): List<RecipeVersion>

}