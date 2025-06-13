package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.domain.Ingredient
import com.plcoding.bookpedia.recipe.domain.MeasureUnit
import com.plcoding.bookpedia.recipe.domain.StandardIngredient

// ## only pass measureunits valid for this type of ingredient (volume, density...)

@Composable
fun EditableIngredientItem(
    ingredient: Ingredient,
    measureUnits: List<MeasureUnit>,
    standardIngredientResults: List<StandardIngredient>,
    onUpdate: (Ingredient) -> Unit,
    onDelete: () -> Unit,
    onSearchStandardIngredient: (String) -> Unit,
    onSelectStandardIngredient: (StandardIngredient) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        OutlinedTextField(
            value = ingredient.quantity.toString(),
            onValueChange = {
                // Safely update quantity, defaulting to 0.0 if input is invalid
                onUpdate(ingredient.copy(quantity = it.toDoubleOrNull() ?: 0.0))
            },
            label = { Text("Qty") },
            modifier = Modifier.width(80.dp)
        )

        MeasureUnitDropdown(
            units = measureUnits,
            selectedUnit = ingredient.measureUnit,
            onUnitSelected = { newUnit ->
                onUpdate(ingredient.copy(measureUnit = newUnit))
            },
            modifier = Modifier.width(110.dp)
        )

        StandardIngredientAutocomplete(
            value = ingredient.customDisplayName,
            onValueChange = { newName ->
                onUpdate(ingredient.copy(customDisplayName = newName))
                onSearchStandardIngredient(newName)
            },
            searchResults = standardIngredientResults,
            onResultSelected = onSelectStandardIngredient,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Ingredient")
        }
    }
}
