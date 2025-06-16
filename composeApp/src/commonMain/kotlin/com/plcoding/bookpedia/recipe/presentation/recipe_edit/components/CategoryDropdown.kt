package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.plcoding.bookpedia.recipe.domain.Category

//## make max-width only as much as longest text
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    onNewCategoryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedCategory.name,
            onValueChange = {},
            readOnly = true,
            label = { Text("Category") },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, contentDescription = "Open") },
            modifier = Modifier.menuAnchor().clickable { isExpanded = true }
        )
        ExposedDropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category.name) },
                    onClick = {
                        onCategorySelected(category)
                        isExpanded = false
                    }
                )
            }
            // Add a special item for managing categories
            Divider()
            DropdownMenuItem(
                text = { Text("Edit Categories..") },
                onClick = {
                    onNewCategoryClick()
                    isExpanded = false
                }
            )
        }
    }
}