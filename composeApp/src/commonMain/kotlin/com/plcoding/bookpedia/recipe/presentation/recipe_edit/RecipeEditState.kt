package com.plcoding.bookpedia.recipe.presentation.recipe_edit

import com.plcoding.bookpedia.core.presentation.UiText
import com.plcoding.bookpedia.recipe.domain.Category
import com.plcoding.bookpedia.recipe.domain.Ingredient
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.domain.RecipeVersion


data class RecipeEditState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val recipeHeader: RecipeHeader? = null,
    val selectedVersion: RecipeVersion? = null,
    val availableCategories: List<Category> = emptyList(),
    val error: UiText? = null,
    val isFinished: Boolean = false // Becomes true after successful save
)