package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.RecipeEditState
import com.plcoding.bookpedia.recipe.presentation.recipeedit.RecipeEditAction

@Composable
fun DeleteMenu(
    state: RecipeEditState,
    onAction: (RecipeEditAction) -> Unit
) {
    Box {
        IconButton(onClick = { onAction(RecipeEditAction.OnShowDeleteMenu) }) {
            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
        }
        DropdownMenu(
            expanded = state.isDeleteMenuExpanded,
            onDismissRequest = { onAction(RecipeEditAction.OnDismissDeleteMenu) }
        ) {
            DropdownMenuItem(
                text = { Text("Delete This Version") },
                onClick = { onAction(RecipeEditAction.OnDeleteVersionClick) },
                // Disable if there's only one version left
                enabled = state.allVersions.size > 1
            )
            DropdownMenuItem(
                text = { Text("Delete Entire Recipe") },
                onClick = { onAction(RecipeEditAction.OnDeleteRecipeClick) }
            )
        }
    }
}