package com.plcoding.bookpedia.recipe.presentation.recipe_detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.domain.Ingredient

@Composable
fun IngredientItem(
    ingredient: Ingredient,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(
//            #measureUnit fetch
            text = "${ingredient.quantity} ${ingredient.measureUnit.name} ${ingredient.customDisplayName}",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isChecked) Color.Gray else Color.Black,
                fontWeight = if (isChecked) FontWeight.Normal else FontWeight.Bold
            )
        )
    }
}
