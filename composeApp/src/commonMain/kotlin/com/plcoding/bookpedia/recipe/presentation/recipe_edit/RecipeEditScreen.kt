package com.plcoding.bookpedia.recipe.presentation.recipe_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.components.DeleteConfirmationDialog
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.components.DeleteMenu
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.sections.DirectionsSection
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.sections.GeneralInfoSection
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.sections.IngredientsSection
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.sections.TimerSection
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.sections.VersionDetailsSection
import com.plcoding.bookpedia.recipe.presentation.recipeedit.RecipeEditAction

@Composable
fun RecipeEditScreenRoot(
    viewModel: RecipeEditViewModel,
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            onBackClick()
        }
    }

    RecipeEditScreen(
        state = state,
        onAction = { action ->
            if (action is RecipeEditAction.OnBackClick) {
                onBackClick()
            } else {
                viewModel.onAction(action)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeEditScreen(
    state: RecipeEditState,
    onAction: (RecipeEditAction) -> Unit
) {
//    ##replace
    if (state.isCategorySheetOpen) {
        AlertDialog(
            onDismissRequest = { onAction(RecipeEditAction.OnDismissCategoryManager) },
            title = { Text("Category Manager") },
            text = { Text("This screen is not yet implemented. Here you will be able to add and edit categories.") },
            confirmButton = {
                TextButton(onClick = { onAction(RecipeEditAction.OnDismissCategoryManager) }) {
                    Text("Close")
                }
            }
        )
    }

    DeleteConfirmationDialog(state.deleteType, onAction)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Recipe" else "New Recipe") },
                navigationIcon = {
                    IconButton(onClick = { onAction(RecipeEditAction.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
                    } else {

                        TextButton(onClick = { onAction(RecipeEditAction.OnOverwriteVersionClick) }) {
                            Text("Save")
                        }
                        if (state.isEditing) { // Only show "Save as New" when editing an existing recipe
                            TextButton(onClick = { onAction(RecipeEditAction.OnSaveAsNewVersionClick) }) {
                                Text("Save as New")
                            }
                            DeleteMenu(state, onAction)
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Text(
                    text = state.error.asString(),
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                RecipeEditForm(
                    state = state,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun RecipeEditForm(
    state: RecipeEditState,
    onAction: (RecipeEditAction) -> Unit
) {
    // The LazyColumn is now much cleaner, containing only the high-level sections.
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            GeneralInfoSection(
                header = state.recipeHeader,
                categories = state.availableCategories,
//                ##pass in specific action here
                onAction = onAction,
            )
        }

        item {
            VersionDetailsSection(
                version = state.selectedVersion,
                onAction = onAction
            )
        }

        item {
            IngredientsSection(
                ingredients = state.selectedVersion?.ingredients ?: emptyList(),
                measureUnits = state.availableMeasureUnits,
                standardIngredientSearchResults = state.standardIngredientSearchResults,
                onAction = onAction
            )
        }

        item {
            DirectionsSection(
                directions = state.selectedVersion?.directions ?: emptyList(),
                onAction = onAction
            )
        }

        item {
            TimerSection(
                dialogStepIndex = state.editingTimerForStepIndex,
//                # i don't like this complex accessing below.
                timerInfo = state.editingTimerForStepIndex?.let { state.selectedVersion?.directions?.getOrNull(it)?.timerInfo },
                onAction = onAction
            )
        }
    }
}


