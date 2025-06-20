package com.plcoding.bookpedia.recipe.presentation.recipe_detail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.RecipeDetailAction
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import com.plcoding.bookpedia.core.presentation.DarkerMediumGreen
import com.plcoding.bookpedia.core.presentation.LightGreen

@Composable
fun MetaInfoSection(
    categoryName: String,
    versionCommentary: String?,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val hasContent = versionCommentary?.isNotBlank() == true

    // Use a Card for better visual grouping
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = LightGreen.copy(alpha = 0.3f), )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() } // Make the whole section header clickable
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Info, contentDescription = "Information")
                Text(
                    text = categoryName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                // You can add an up/###down arrow here to indicate collapsibility if you like
//                also collapse everything
            }

            // This will smoothly animate the content visibility
            AnimatedVisibility(visible = isExpanded) {
                if(hasContent) {
                    Column {
                        Spacer(Modifier.height(8.dp))
                        Text(
//                            ##make that more professional
                            text = "            $versionCommentary",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}