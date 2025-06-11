package com.plcoding.bookpedia.app


import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.RecipeDetailScreenRoot
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.RecipeDetailViewModel
import com.plcoding.bookpedia.recipe.presentation.recipe_list.RecipeListScreenRoot
import com.plcoding.bookpedia.recipe.presentation.recipe_list.RecipeListViewModel

import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = RecipeList // starting screen
        ) {
            composable<RecipeList>(
                exitTransition = { slideOutHorizontally() },
                popEnterTransition = { slideInHorizontally() }
            ) {
                // Get ViewModel using Koin
                val viewModel = koinViewModel<RecipeListViewModel>()

                RecipeListScreenRoot(
                    viewModel = viewModel,
                    onRecipeClick = { recipeHeader ->
                        // When a recipe is clicked, navigate to the detail screen
                        // and pass the recipe's header ID
                        navController.navigate(
                            RecipeDetail(recipeHeaderId = recipeHeader.id)
                        )
                    }
                )
            }

            composable<RecipeDetail>(
                enterTransition = { slideInHorizontally { initialOffset -> initialOffset } },
                exitTransition = { slideOutHorizontally { initialOffset -> initialOffset } }
            ) {
                // Get ViewModel using Koin. Koin will automatically provide the
                // SavedStateHandle, which the ViewModel uses to get the recipeHeaderId.
                val viewModel = koinViewModel<RecipeDetailViewModel>()

                RecipeDetailScreenRoot(
                    viewModel = viewModel,
                    onBackClick = {
                        navController.navigateUp()
                    },
                    onEditClick = { recipeHeaderId ->
                        // TODO: Navigate to an edit screen later
                        // navController.navigate(RecipeEdit(recipeHeaderId))
                    }
                )
            }
        }
    }
}