package com.plcoding.bookpedia.recipe.presentation.recipeedit

import com.plcoding.bookpedia.recipe.domain.Category
import com.plcoding.bookpedia.recipe.domain.Ingredient
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.domain.StandardIngredient

sealed interface RecipeEditAction {
    // Header Actions
    data class OnTitleChanged(val title: String) : RecipeEditAction
    data class OnCategoryChanged(val category: Category) : RecipeEditAction
    data class OnPrepTimeChanged(val time: String) : RecipeEditAction

    // Version Actions
    data class OnVersionNameChanged(val name: String) : RecipeEditAction
    data class OnVersionCommentaryChanged(val commentary: String) : RecipeEditAction

    // Ingredient Actions
    data object OnAddNewIngredient : RecipeEditAction
    data class OnDeleteIngredient(val index: Int) : RecipeEditAction
    data class OnUpdateIngredient(val index: Int, val ingredient: Ingredient) : RecipeEditAction
    data class OnSearchStandardIngredient(val query: String) : RecipeEditAction
    data class OnSelectStandardIngredient(val index: Int, val standardIngredient: StandardIngredient): RecipeEditAction
    // Timer Actions
    data class OnShowTimerDialog(val stepIndex: Int) : RecipeEditAction
    data object OnDismissTimerDialog : RecipeEditAction
    data class OnSaveTimer(val hours: Int, val minutes: Int, val seconds: Int) : RecipeEditAction
    data object OnDeleteTimer : RecipeEditAction

    // Direction Actions
    data object OnAddNewDirection : RecipeEditAction
    data class OnDeleteDirection(val index: Int) : RecipeEditAction
    data class OnUpdateDirection(val index: Int, val step: InstructionStep) : RecipeEditAction

    // Category Actions
    data object OnCategoryManagerClick : RecipeEditAction
    data object OnDismissCategoryManager : RecipeEditAction

    // Main Actions
    data object OnOverwriteVersionClick : RecipeEditAction
    data object OnSaveAsNewVersionClick : RecipeEditAction
    data object OnBackClick : RecipeEditAction
}
