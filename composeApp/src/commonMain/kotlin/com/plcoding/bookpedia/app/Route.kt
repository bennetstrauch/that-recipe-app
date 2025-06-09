package com.plcoding.bookpedia.app

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route

@Serializable
data object RecipeList : Route

@Serializable
data class RecipeDetail(val recipeHeaderId: String) : Route

// You can add more routes here later, like an Edit screen
// @Serializable
// data class RecipeEdit(val recipeHeaderId: String) : Route