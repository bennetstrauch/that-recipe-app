package com.plcoding.bookpedia.recipe.presentation.recipe_detail

sealed interface RecipeDetailEvent {
    data object PlayAlarmSound : RecipeDetailEvent
    // You could add others here later, e.g., data class ShowSnackbar(val message: UiText) : RecipeDetailEvent
}