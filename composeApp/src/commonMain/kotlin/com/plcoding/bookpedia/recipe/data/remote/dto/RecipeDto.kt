package com.plcoding.bookpedia.recipe.data.remote.dto

import kotlinx.serialization.Serializable

// This DTO structure matches what we will ask the AI to return as JSON
@Serializable
data class RecipeDto(
    val title: String,
    val category: String,
    val prepTimeMinutes: Int?,
    val ingredients: List<IngredientDto>,
    val directions: List<DirectionDto>
)

@Serializable
data class IngredientDto(
    val name: String,
    // Kept as a string, as AI might return "a pinch" or "1-2" ?##
    val quantity: String,
    val unit: String
)

@Serializable
data class DirectionDto(
    val description: String,
    val timerInMinutes: Int? = null
)