package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.plcoding.bookpedia.recipe.presentation.recipe_edit.DeleteType
import com.plcoding.bookpedia.recipe.presentation.recipeedit.RecipeEditAction

@Composable
fun DeleteConfirmationDialog(
    deleteType: DeleteType?,
    onAction: (RecipeEditAction) -> Unit
) {
    if (deleteType != null) {
        val title = if(deleteType == DeleteType.VERSION) "Delete Version?" else "Delete Recipe?"
        val text = if(deleteType == DeleteType.VERSION) "Are you sure you want to permanently delete this version?" else "Are you sure you want to permanently delete this recipe and all of its versions?"

        AlertDialog(
            onDismissRequest = { onAction(RecipeEditAction.OnDismissDeleteConfirmation) },
            title = { Text(title) },
            text = { Text(text) },
            confirmButton = {
                Button(
                    onClick = { onAction(RecipeEditAction.OnConfirmDelete) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(RecipeEditAction.OnDismissDeleteConfirmation) }) {
                    Text("Cancel")
                }
            }
        )
    }
}