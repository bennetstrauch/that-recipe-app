package com.plcoding.bookpedia.recipe.presentation.recipe_list

import com.plcoding.bookpedia.core.presentation.UiText
import com.plcoding.bookpedia.recipe.domain.RecipeHeader

data class RecipeListState (
    val searchQuery: String = "",
    val searchResults: List<RecipeHeader> = emptyList(),
    val favoriteRecipes: List<RecipeHeader> = emptyList(),
    val selectedTabIndex: Int = 0,
    val errorMessage: UiText? = null,
    val isLoading: Boolean = true,

    )