package com.plcoding.bookpedia.recipe.presentation.recipe_detail.components
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.plcoding.bookpedia.recipe.presentation.util.formatDuration


@Composable
fun TimerItem(
    isRunning: Boolean,
    remainingSeconds: Long?,
    staticText: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        if (isRunning && remainingSeconds != null) {
            Text(
                text = formatDuration(remainingSeconds),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        } else {
            Text(
                text = staticText,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        IconButton(onClick = onClick) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "Start Timer",
                tint = if (isRunning) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
        }
    }
}

