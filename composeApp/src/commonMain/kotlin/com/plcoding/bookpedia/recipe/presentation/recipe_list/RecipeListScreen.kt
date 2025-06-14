package com.plcoding.bookpedia.recipe.presentation.recipe_list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.bookpedia.core.presentation.UiText // Assuming these exist
import com.plcoding.bookpedia.recipe.domain.Category
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.presentation.recipe_list.components.RecipeSearchBar
import com.plcoding.recipepedia.recipe.presentation.recipe_list.components.RecipeList
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecipeListScreenRoot(
    viewModel: RecipeListViewModel = koinViewModel(),
    onRecipeClick: (RecipeHeader) -> Unit,
    onAddRecipeClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    RecipeListScreen(
        state = state,
        onAction = { action ->
            when (action) {
                is RecipeListAction.OnRecipeClick -> onRecipeClick(action.recipe)
                is RecipeListAction.OnAddRecipeClick -> onAddRecipeClick() // <-- HANDLE THE NEW ACTION
                else -> viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListScreen(
    state: RecipeListState,
    onAction: (RecipeListAction) -> Unit,
){
    val keyboardController = LocalSoftwareKeyboardController.current

    val pagerState = rememberPagerState { 2 }
    val searchResultsListState = rememberLazyListState()
    val favoriteRecipesListState = rememberLazyListState()

    // Sync the pager with the selected tab index from the state
    LaunchedEffect(state.selectedTabIndex) {
        pagerState.animateScrollToPage(state.selectedTabIndex)
    }

    // Inform the ViewModel when the user swipes the pager
    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            onAction(RecipeListAction.OnTabSelected(pagerState.currentPage))
        }
    }

    Scaffold(
        topBar = {
            RecipeSearchBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = {
                    onAction(RecipeListAction.OnSearchQueryChange(it))
                },
                onImeSearch = {
                    keyboardController?.hide()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(10.dp)
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onAction(RecipeListAction.OnAddRecipeClick)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new recipe"
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = state.selectedTabIndex,
            ) {
                Tab(
                    selected = state.selectedTabIndex == 0,
                    onClick = { onAction(RecipeListAction.OnTabSelected(0)) },
                    text = { Text("Search Results") }
                )
                Tab(
                    selected = state.selectedTabIndex == 1,
                    onClick = { onAction(RecipeListAction.OnTabSelected(1)) },
                    text = { Text("Favorites") }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { pageIndex ->
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when(pageIndex) {
                        0 -> { // Search Results Tab
                            if(state.isLoading) {
                                CircularProgressIndicator()
                            } else if (state.errorMessage != null) {
                                Text(
                                    text = state.errorMessage.asString(), // Correctly uses UiText
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error
                                )
                            } else if (state.searchQuery.isNotBlank() && state.searchResults.isEmpty()) {
                                Text("No results for '${state.searchQuery}'")
                            }
                            else {
                                RecipeList(
                                    recipes = state.searchResults,
                                    onRecipeClick = {
                                        onAction(RecipeListAction.OnRecipeClick(it))
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    scrollState = searchResultsListState
                                )
                            }
                        }
                        1 -> { // Favorites Tab
                            if(state.favoriteRecipes.isEmpty()) {
                                Text("You have no favorite recipes yet.")
                            } else {
                                RecipeList(
                                    recipes = state.favoriteRecipes,
                                    onRecipeClick = {
                                        onAction(RecipeListAction.OnRecipeClick(it))
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    scrollState = favoriteRecipesListState
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


