package com.plcoding.bookpedia.app

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route

@Serializable
data object RecipeList : Route

@Serializable
data class RecipeDetail(val recipeHeaderId: String) : Route

 @Serializable
 data class RecipeEdit(val recipeHeaderId: String, val recipeVersionId: String) : Route