package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.plcoding.bookpedia.recipe.domain.StandardIngredient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardIngredientAutocomplete(
    value: String,
    onValueChange: (String) -> Unit,
    searchResults: List<StandardIngredient>,
    onResultSelected: (StandardIngredient) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
                // Always show the dropdown when the user is typing
                isExpanded = it.isNotBlank() && searchResults.isNotEmpty()
            },
            label = { Text("Ingredient") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        // Only show the dropdown if there are results
        if (searchResults.isNotEmpty()) {
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                searchResults.forEach { standardIngredient ->
                    DropdownMenuItem(
                        text = { Text(standardIngredient.name) },
                        onClick = {
                            onResultSelected(standardIngredient)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}