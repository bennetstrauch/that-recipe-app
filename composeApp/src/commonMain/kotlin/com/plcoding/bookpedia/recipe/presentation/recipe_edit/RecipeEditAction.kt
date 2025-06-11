package com.plcoding.bookpedia.recipe.presentation.recipeedit

import com.plcoding.bookpedia.recipe.domain.Category
import com.plcoding.bookpedia.recipe.domain.Ingredient
import com.plcoding.bookpedia.recipe.domain.InstructionStep

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

    // Direction Actions
    data object OnAddNewDirection : RecipeEditAction
    data class OnDeleteDirection(val index: Int) : RecipeEditAction
    data class OnUpdateDirection(val index: Int, val step: InstructionStep) : RecipeEditAction

    // Main Actions
    data object OnOverwriteVersionClick : RecipeEditAction
    data object OnSaveAsNewVersionClick : RecipeEditAction
    data object OnBackClick : RecipeEditAction
}
