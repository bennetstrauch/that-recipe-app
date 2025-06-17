package com.plcoding.bookpedia.recipe.presentation.recipe_list

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import com.plcoding.bookpedia.recipe.domain.ParseRecipeFromUrlUseCase
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import com.plcoding.bookpedia.recipe.presentation.recipeedit.RecipeEditAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class RecipeListViewModel(
    private val recipeRepository: RecipeRepository,
    private val parseRecipeFromUrlUseCase: ParseRecipeFromUrlUseCase // Ensure this is injected via Koin
) : ViewModel() {

    companion object{
        private const val DEFAULT_PREVIEW_URL = "https://www.skinnytaste.com/"
    }

    private val _state = MutableStateFlow(RecipeListState())
    val state = _state.asStateFlow()

    init {
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
                _state.update { it.copy(isAddRecipeMenuExpanded = !state.value.isAddRecipeMenuExpanded) }
            }
            is RecipeListAction.OnCreateFromImageClick -> {
                println("IMAGE-CREATE CLICK")
            }
            is RecipeListAction.OnCreateFromScratchClick -> {
                _state.update {it.copy(isAddRecipeMenuExpanded = false)}
            }
            is RecipeListAction.OnCreateFromUrlClick -> createFromUrlClick()
            is RecipeListAction.OnDismissAddRecipeMenu -> {
                _state.update { it.copy(isAddRecipeMenuExpanded = false) }
            }
            is RecipeListAction.OnNavigatedToEditScreen -> {
                _state.update { it.copy(parsedRecipeId = null) }
            }
//            is RecipeListAction.OnParseDialogDismiss -> parseDialogDismiss()
//            is RecipeListAction.OnParseUrl -> parseUrl(action.url)
            is RecipeListAction.OnUrlEntered -> {
                _state.update { it.copy(urlToPreview = action.url) }
            }
            is RecipeListAction.OnPreviewUrl -> {
                _state.update { it.copy(isWebPreviewDialogOpen = true) }
            }
            is RecipeListAction.OnDismissWebPreviewDialog -> {
                _state.update { it.copy(isWebPreviewDialogOpen = false) }
            }
            is RecipeListAction.OnParseFromPreview -> {
                parseUrl(state.value.urlToPreview)
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


    private fun createFromUrlClick() {
        _state.update {
            it.copy(
                isWebPreviewDialogOpen = true,
//                isParseUrlDialogOpen = true,
                isAddRecipeMenuExpanded = false,
                urlToPreview = DEFAULT_PREVIEW_URL
            )
        }
    }

    private fun parseDialogDismiss(): Unit {
        _state.update {
            it.copy(
                isWebPreviewDialogOpen = false,

//                isParseUrlDialogOpen = false,
                isParsing = false,
                parseError = null
            )
        }
    }


    private fun parseUrl(url: String) {
        viewModelScope.launch {
            _state.update { it.copy(isParsing = true, parseError = null) }

            val result = parseRecipeFromUrlUseCase(url)

            result.onSuccess { (header, version) ->
                // After parsing, save the new recipe to the database
                val saveResult = recipeRepository.createNewRecipe(header, version)
                saveResult.onSuccess { newHeaderId ->
                    // On successful save, update the state with the new ID to trigger navigation
                    _state.update {
                        it.copy(
                            isParsing = false,
                            isWebPreviewDialogOpen = false,

//                            isParseUrlDialogOpen = false,
                            parsedRecipeId = newHeaderId // This triggers navigation
                        )
                    }
                }.onError { error ->
                    _state.update { it.copy(isParsing = false, parseError = error.toUiText()) }
                }
            }.onError { error ->
                _state.update { it.copy(isParsing = false, parseError = error.toUiText()) }
            }
        }
    }





}