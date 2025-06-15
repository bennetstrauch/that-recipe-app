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
import com.plcoding.bookpedia.recipe.presentation.util.formatDuration

@Composable
fun DirectionStepItem(
    step: InstructionStep,
    isRunning: Boolean,
    isPaused: Boolean,
    remainingSeconds: Long?,
    isChecked: Boolean,
    onAction: (RecipeDetailAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onAction(RecipeDetailAction.OnToggleStepCheck(step.id)) }
        )
        Text(text = step.description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
//                #need to copy style here?
                color = if (isChecked) Color.Gray else Color.Black
            )
        )

        if (step.timerInfo != null) {
            TimerItem(
                isRunning=isRunning, remainingSeconds=remainingSeconds ?: 0, staticText = "",
                isPaused = isPaused,
                onClick = { onAction(RecipeDetailAction.OnTimerClick(step.id)) },
                onPause = { onAction(RecipeDetailAction.OnPauseTimer(step.id)) },
                onResume = { onAction(RecipeDetailAction.OnResumeTimer(step.id)) },
            )
        }
    }
}