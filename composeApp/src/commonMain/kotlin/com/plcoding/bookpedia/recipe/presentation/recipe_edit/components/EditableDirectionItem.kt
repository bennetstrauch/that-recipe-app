package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.presentation.util.formatDuration

@Composable
fun EditableDirectionItem(
    step: InstructionStep,
    onUpdate: (InstructionStep) -> Unit,
    onDelete: () -> Unit,
    onEditTimerClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        OutlinedTextField(
            value = step.description,
            onValueChange = { onUpdate(step.copy(description = it)) },
            label = { Text("Step Description") },
            modifier = Modifier.weight(1f)
        )
        // TODO: Add UI to edit the timerInfo (e.g., a small text field for seconds)
        // For now, this just shows an icon if a timer exists.
        if(step.timerInfo != null) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Has Timer"
            )
        }

        // Timer Button
//        TextButton(onClick = onEditTimerClick) {
//            Icon(
//                imageVector = Icons.Default.Schedule,
//                contentDescription = "Edit Timer",
//                tint = if (step.timerInfo != null) MaterialTheme.colorScheme.primary else LocalContentColor.current
//            )
//            // Display current time or "Edit"
//            val timerDuration = step.timerInfo?.durationSeconds
//            if (timerDuration != null) {
//                val buttonText = formatDuration(timerDuration)
//                Text(buttonText)
//            }
//
//        }

//        #maybe move to left
        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Delete Direction")
        }
    }
}
