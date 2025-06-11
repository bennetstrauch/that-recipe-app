package com.plcoding.bookpedia.recipe.presentation.recipe_edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.bookpedia.recipe.domain.Ingredient
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.presentation.recipeedit.RecipeEditAction

@Composable
fun RecipeEditScreenRoot(
    viewModel: RecipeEditViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Listen for the "isFinished" flag to navigate back after a successful save.
    LaunchedEffect(state.isFinished) {
        if(state.isFinished) {
            onBackClick()
        }
    }

    RecipeEditScreen(
        state = state,
        onAction = { action ->
            if(action is RecipeEditAction.OnBackClick) {
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
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.recipeHeader?.id != null) "Edit Recipe" else "New Recipe") },
                navigationIcon = {
                    IconButton(onClick = { onAction(RecipeEditAction.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(modifier = Modifier.padding(horizontal = 16.dp))
                    } else {
                        // Provides the two save options from your UI design
                        TextButton(onClick = { onAction(RecipeEditAction.OnOverwriteVersionClick) }) {
                            Text("Save")
                        }
                        TextButton(onClick = { onAction(RecipeEditAction.OnSaveAsNewVersionClick) }) {
                            Text("Save as New")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.recipeHeader != null && state.selectedVersion != null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- General Info Section ---
                item {
                    Text("General Information", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = state.recipeHeader.title,
                        onValueChange = { onAction(RecipeEditAction.OnTitleChanged(it)) },
                        label = { Text("Recipe Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    // TODO: Add a dropdown here to select from state.availableCategories
                }

                // --- Version Details Section ---
                item {
                    Text("Version Details", style = MaterialTheme.typography.titleLarge)
                    OutlinedTextField(
                        value = state.selectedVersion.versionName,
                        onValueChange = { onAction(RecipeEditAction.OnVersionNameChanged(it)) },
                        label = { Text("Version Name (e.g., Original, Low Carb)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.selectedVersion.versionCommentary ?: "",
                        onValueChange = { onAction(RecipeEditAction.OnVersionCommentaryChanged(it)) },
                        label = { Text("Notes for this version") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // --- Ingredients Section ---
                item {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Ingredients", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onAction(RecipeEditAction.OnAddNewIngredient) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
                        }
                    }
                }
                itemsIndexed(state.selectedVersion.ingredients) { index, ingredient ->
                    EditableIngredientItem(
                        ingredient = ingredient,
                        onUpdate = { updatedIngredient -> onAction(RecipeEditAction.OnUpdateIngredient(index, updatedIngredient)) },
                        onDelete = { onAction(RecipeEditAction.OnDeleteIngredient(index)) }
                    )
                }

                // --- Directions Section ---
                item {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Directions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onAction(RecipeEditAction.OnAddNewDirection) }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Direction")
                        }
                    }
                }
                itemsIndexed(state.selectedVersion.directions) { index, step ->
                    EditableDirectionItem(
                        step = step,
                        onUpdate = { updatedStep -> onAction(RecipeEditAction.OnUpdateDirection(index, updatedStep)) },
                        onDelete = { onAction(RecipeEditAction.OnDeleteDirection(index)) }
                    )
                }
            }
        } else {
            // Handle error state or initial "new recipe" state
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                state.error?.let {
                    Text(text = it.asString(), color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun EditableIngredientItem(
    ingredient: Ingredient,
    onUpdate: (Ingredient) -> Unit,
    onDelete: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = ingredient.quantity.toString(),
            onValueChange = { onUpdate(ingredient.copy(quantity = it.toDoubleOrNull() ?: 0.0)) },
            label = { Text("Qty") },
            modifier = Modifier.width(80.dp)
        )
        // TODO: Replace with a Dropdown for MeasureUnit
        OutlinedTextField(
            value = ingredient.measureUnit.abbreviation ?: "",
            onValueChange = { /* Implement changing unit */ },
            label = { Text("Unit") },
            modifier = Modifier.width(90.dp)
        )
        OutlinedTextField(
            value = ingredient.customDisplayName,
            onValueChange = { onUpdate(ingredient.copy(customDisplayName = it)) },
            label = { Text("Ingredient") },
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Ingredient")
        }
    }
}

@Composable
private fun EditableDirectionItem(
    step: InstructionStep,
    onUpdate: (InstructionStep) -> Unit,
    onDelete: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(
            value = step.description,
            onValueChange = { onUpdate(step.copy(description = it)) },
            label = { Text("Step Description") },
            modifier = Modifier.weight(1f)
        )
        // TODO: Add UI to edit the timerInfo (e.g., a small text field for seconds)
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Direction")
        }
    }
}
