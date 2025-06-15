package com.plcoding.bookpedia.recipe.presentation.recipe_detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.RecipeDetailAction

@Composable
fun DirectionStepItem(
    step: InstructionStep,
    isRunning: Boolean,
    remainingSeconds: Long?,
    isChecked: Boolean,
    onAction: (RecipeDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onAction(RecipeDetailAction.OnToggleStepCheck(step.id)) }
        )
        Text(
            text = step.description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isChecked) Color.Gray else LocalContentColor.current
        )

        // Timer display and button
        if (step.timerInfo != null) {
            if (isRunning && remainingSeconds != null) {
                Text(
                    text = formatDuration(remainingSeconds),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { onAction(RecipeDetailAction.OnTimerClick(step.id)) }) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Start/Stop Step Timer",
                    tint = if (isRunning) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    }
}

private fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "$hours:${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    } else {
        "${minutes}:${seconds.toString().padStart(2, '0')}"
    }
}