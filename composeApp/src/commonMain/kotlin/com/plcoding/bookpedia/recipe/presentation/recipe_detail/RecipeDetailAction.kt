package com.plcoding.bookpedia.recipe.presentation.recipe_detail

import com.plcoding.bookpedia.recipe.domain.RecipeHeader

sealed interface RecipeDetailAction {
//    ## remove data form object if not serialized.
    data object OnBackClick : RecipeDetailAction
    data object OnEditClick : RecipeDetailAction
    data object OnTogglePictureVisibility : RecipeDetailAction
    data class OnSelectVersion(val versionId: String) : RecipeDetailAction
    data class OnToggleIngredientCheck(val ingredientId: String) : RecipeDetailAction
    data class OnToggleStepCheck(val stepId: String) : RecipeDetailAction
//    ##should we have timerActions separate?
    data class OnTimerClick(val stepId: String) : RecipeDetailAction
    data class OnPauseTimer(val stepId: String) : RecipeDetailAction
    data class OnResumeTimer(val stepId: String) : RecipeDetailAction
}