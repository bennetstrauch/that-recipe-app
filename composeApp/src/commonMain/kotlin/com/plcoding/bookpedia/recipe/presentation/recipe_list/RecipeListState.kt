package com.plcoding.bookpedia.recipe.presentation.recipe_list

import com.plcoding.bookpedia.core.presentation.UiText
import com.plcoding.bookpedia.recipe.domain.RecipeHeader

data class RecipeListState (
    val searchQuery: String = "",
    val searchResults: List<RecipeHeader> = emptyList(),
    val favoriteRecipes: List<RecipeHeader> = emptyList(),
    val selectedTabIndex: Int = 0,
    val isLoading: Boolean = true,

    val errorMessage: UiText? = null,

//    ADDING:
    val isAddRecipeMenuExpanded: Boolean = false, // To control the new FAB menu
    val isParsing: Boolean = false,

//    val isParseUrlDialogOpen: Boolean = false,
    val isWebPreviewDialogOpen: Boolean = false,
    val urlToPreview: String = "",

    val parsedRecipeId: String? = null, // To trigger navigation after a successful parse
    val parseError: UiText? = null


    )