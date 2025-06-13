package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.plcoding.bookpedia.recipe.domain.MeasureUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasureUnitDropdown(
    units: List<MeasureUnit>,
    selectedUnit: MeasureUnit,
    onUnitSelected: (MeasureUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedUnit.abbreviation ?: selectedUnit.name,
            onValueChange = {}, // Read-only, selection happens in the menu
            readOnly = true,
            label = { Text("Unit") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Open")
            },
            modifier = Modifier
                .menuAnchor() // This is important for correct positioning
                .clickable { isExpanded = true }
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text("${unit.name} (${unit.abbreviation})") },
                    onClick = {
                        onUnitSelected(unit)
                        isExpanded = false
                    }
                )
            }
        }
    }
}