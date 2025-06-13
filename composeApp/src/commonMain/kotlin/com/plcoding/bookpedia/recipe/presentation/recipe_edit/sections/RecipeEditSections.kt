package com.plcoding.bookpedia.recipe.presentation.recipe_edit.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.domain.Ingredient
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.domain.MeasureUnit
import com.plcoding.bookpedia.recipe.domain.RecipeHeader
import com.plcoding.bookpedia.recipe.domain.RecipeVersion
import com.plcoding.bookpedia.recipe.domain.StandardIngredient
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.components.EditableDirectionItem
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.components.EditableIngredientItem
import com.plcoding.bookpedia.recipe.presentation.recipeedit.RecipeEditAction


@Composable
fun GeneralInfoSection(
    // This component now only takes the specific data it needs
    header: RecipeHeader?,
    onAction: (RecipeEditAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("General Information", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = header?.title ?: "",
            onValueChange = { onAction(RecipeEditAction.OnTitleChanged(it)) },
            label = { Text("Recipe Title") },
            modifier = Modifier.fillMaxWidth()
        )
        // TODO: Add a dropdown here to select from available categories
    }
}

@Composable
fun VersionDetailsSection(
    // This component now only takes the specific data it needs
    version: RecipeVersion?,
    onAction: (RecipeEditAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Version Details", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = version?.versionName ?: "",
            onValueChange = { onAction(RecipeEditAction.OnVersionNameChanged(it)) },
            label = { Text("Version Name (e.g., Original, Low Carb)") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = version?.versionCommentary ?: "",
            onValueChange = { onAction(RecipeEditAction.OnVersionCommentaryChanged(it)) },
            label = { Text("Notes for this version") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun IngredientsSection(
    ingredients: List<Ingredient>,
    measureUnits: List<MeasureUnit>,
    standardIngredientSearchResults: List<StandardIngredient>,
    onAction: (RecipeEditAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Ingredients", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = { onAction(RecipeEditAction.OnAddNewIngredient) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Ingredient")
            }
        }
        ingredients.forEachIndexed { index, ingredient ->
            EditableIngredientItem(
                ingredient = ingredient,
                measureUnits = measureUnits,
                standardIngredientResults = standardIngredientSearchResults,
                onUpdate = { updatedIngredient -> onAction(RecipeEditAction.OnUpdateIngredient(index, updatedIngredient)) },
                onDelete = { onAction(RecipeEditAction.OnDeleteIngredient(index)) },
                onSearchStandardIngredient = { query -> onAction(RecipeEditAction.OnSearchStandardIngredient(query)) },
                onSelectStandardIngredient = {selectedStandardIngredient -> onAction(RecipeEditAction.OnSelectStandardIngredient(index, selectedStandardIngredient))},
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
fun DirectionsSection(
    directions: List<InstructionStep>,
    onAction: (RecipeEditAction) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Directions", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
            IconButton(onClick = { onAction(RecipeEditAction.OnAddNewDirection) }) {
                Icon(Icons.Default.Add, contentDescription = "Add Direction")
            }
        }
        directions.forEachIndexed { index, step ->
            EditableDirectionItem(
                step = step,
                onUpdate = { updatedStep -> onAction(RecipeEditAction.OnUpdateDirection(index, updatedStep)) },
                onDelete = { onAction(RecipeEditAction.OnDeleteDirection(index)) },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}