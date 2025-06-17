package com.plcoding.bookpedia.recipe.presentation.recipe_list

import com.plcoding.bookpedia.recipe.domain.RecipeHeader

sealed interface RecipeListAction {
    data class OnSearchQueryChange(val query: String) : RecipeListAction
    data class OnRecipeClick(val recipe: RecipeHeader) : RecipeListAction //?# better pass id?
    data class OnTabSelected(val tabIndex: Int) : RecipeListAction
    //    Adding:
    data object OnAddRecipeClick : RecipeListAction
    data object OnDismissAddRecipeMenu : RecipeListAction
    data object OnCreateFromScratchClick : RecipeListAction
    data object OnCreateFromUrlClick : RecipeListAction
    data object OnCreateFromImageClick : RecipeListAction // For the future
    data object OnParseDialogDismiss : RecipeListAction
    data class OnParseUrl(val url: String) : RecipeListAction
    data object OnNavigatedToEditScreen: RecipeListAction
}