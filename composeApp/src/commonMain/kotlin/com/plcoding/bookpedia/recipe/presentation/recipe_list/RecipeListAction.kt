package com.plcoding.bookpedia.recipe.presentation.recipe_list

import com.plcoding.bookpedia.recipe.domain.RecipeHeader

sealed interface RecipeListAction {
    data class OnSearchQueryChange(val query: String) : RecipeListAction
    data class OnRecipeClick(val recipe: RecipeHeader) : RecipeListAction //?# better pass id?
    data class OnTabSelected(val tabIndex: Int) : RecipeListAction
}