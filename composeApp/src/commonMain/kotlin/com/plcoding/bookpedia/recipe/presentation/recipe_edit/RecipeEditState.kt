package com.plcoding.bookpedia.recipe.presentation.recipe_edit

import com.plcoding.bookpedia.core.presentation.UiText
import com.plcoding.bookpedia.recipe.domain.Category
import com.plcoding.bookpedia.recipe.domain.MeasureUnit
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.domain.RecipeVersion
import com.plcoding.bookpedia.recipe.domain.StandardIngredient


data class RecipeEditState(

    val recipeHeader: RecipeHeader? = null,
    val selectedVersion: RecipeVersion? = null,
    val allVersions: List<RecipeVersion> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val availableMeasureUnits: List<MeasureUnit> = emptyList(),
    val standardIngredientSearchResults: List<StandardIngredient> = emptyList(),
    val editingTimerForStepIndex: Int? = null,

    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val isFinished: Boolean = false, // Becomes true after successful save
    val isEditing: Boolean = false, // Becomes true after loading a recipe for editing

    val error: UiText? = null,
)