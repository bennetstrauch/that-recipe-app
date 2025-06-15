package com.plcoding.bookpedia.recipe.presentation.recipe_detail.components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.RecipeDetailAction
import com.plcoding.bookpedia.recipe.presentation.util.formatDuration

//##passing too many values, how to improve? wrap in object?
@Composable
fun TimerItem(
    isRunning: Boolean,
    isPaused: Boolean,
    remainingSeconds: Long,
    staticText: String,
    onClick: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        when {
            isRunning && !isPaused -> {
                Text(
                    text = formatDuration(remainingSeconds),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onPause) {
                    Icon(Icons.Default.Pause, contentDescription = "Pause Timer")
                }
            }
            remainingSeconds > 0 -> {
                Text(
                    text = formatDuration(remainingSeconds),
                    style = MaterialTheme.typography.titleMedium
                )
                IconButton(onClick = onResume) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Resume Timer")
                }
            }
            else -> {
                Text(
                    text = staticText,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        IconButton(onClick = onClick) {
            when{
                isRunning -> Icon(
                    imageVector = Icons.Default.RestartAlt,
                    contentDescription = "Reset Timer",
                    tint = if (isRunning) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
                else -> Icon(
                    imageVector = Icons.Default.PlayArrow,
//                    imageVector = Icons.Default.Schedule, //#whats-better?

                    contentDescription = "Start Timer",
                    tint = if (isRunning) MaterialTheme.colorScheme.primary else LocalContentColor.current
                )
            }
        }
    }
}

