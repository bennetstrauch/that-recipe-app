package com.plcoding.bookpedia.recipe.presentation.recipe_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class RecipeListViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeListState())
    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        // Start observing immediately when the ViewModel is created
        observeSearchQuery()
        observeFavoriteRecipes()
    }

    fun onAction(action: RecipeListAction) {
        when (action) {
            is RecipeListAction.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = action.query) }
            }
            is RecipeListAction.OnTabSelected -> {
                _state.update { it.copy(selectedTabIndex = action.tabIndex) }
            }
            is RecipeListAction.OnRecipeClick -> {
                // This action is handled by the NavHost for navigation,
                // but you could add logic here if needed.
            }
            is RecipeListAction.OnAddRecipeClick -> {
                // action handled by NavHost
            }
        }
    }

    private fun observeFavoriteRecipes() {
        // This will continuously listen for changes in the favorites list
        // and update the UI state automatically.
        recipeRepository.getFavoriteRecipeHeaders()
            .onEach { favoriteRecipes ->
                _state.update { it.copy(favoriteRecipes = favoriteRecipes) }
            }
            .launchIn(viewModelScope)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSearchQuery() {
        state
            .map { it.searchQuery }
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { _state.update { it.copy(isLoading = true) } }
            .flatMapLatest { query -> // flatMapLatest is key: it cancels the old flow and starts a new one
                if (query.isBlank()) {
                    recipeRepository.getAllRecipeHeaders()
                } else {
                    recipeRepository.searchRecipes(query)
                }
            }
            .onEach { result ->
                result.onSuccess { recipes ->
                    _state.update { it.copy(isLoading = false, searchResults = recipes) }
                }.onError { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.toUiText()) }
                }
            }
            .launchIn(viewModelScope)
    }





}