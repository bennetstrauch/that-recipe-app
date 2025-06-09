package com.plcoding.bookpedia.recipe.presentation.recipe_list

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class RecipeListViewModel : ViewModel() {

    private val _state = MutableStateFlow(RecipeListState())
    val state = _state.asStateFlow()

    fun onAction(action: RecipeListAction) {
        when (action) {
            is RecipeListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is RecipeListAction.OnRecipeClick -> {

            }
            is RecipeListAction.OnTabSelected -> {
                _state.update { it.copy(selectedTabIndex = action.tabIndex) }

            }
        }
    }

}