package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.plcoding.bookpedia.recipe.domain.TimerInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimerPickerDialog(
    initialTimerInfo: TimerInfo?,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (hours: Int, minutes: Int, seconds: Int) -> Unit
) {
    // Calculate initial values
    val initialHours = initialTimerInfo?.let { (it.durationSeconds / 3600) } ?: 0L
    val initialMinutes = initialTimerInfo?.let { ((it.durationSeconds % 3600) / 60) } ?: 0L
    val initialSeconds = initialTimerInfo?.let { (it.durationSeconds % 60) } ?: 0L

    // Hold the state of each wheel picker
    var selectedHour by remember { mutableStateOf(initialHours.toInt()) }
    var selectedMinute by remember { mutableStateOf(initialMinutes.toInt()) }
    var selectedSecond by remember { mutableStateOf(initialSeconds.toInt()) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 8.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Edit Timer", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelPicker(
                        items = (0..23).toList(),
                        initialItem = selectedHour,
                        onItemSelected = { selectedHour = it },
                        label = "H"
                    )
                    Text(":", style = MaterialTheme.typography.headlineMedium)
                    WheelPicker(
                        items = (0..59).toList(),
                        initialItem = selectedMinute,
                        onItemSelected = { selectedMinute = it },
                        label = "M"
                    )
                    Text(":", style = MaterialTheme.typography.headlineMedium)
                    WheelPicker(
                        items = (0..59).toList(),
                        initialItem = selectedSecond,
                        onItemSelected = { selectedSecond = it },
                        label = "S"
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDelete) {
                        Text("Delete")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        onSave(
                            selectedHour,
                            selectedMinute,
                            selectedSecond,
                        )
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WheelPicker(
    items: List<Int>,
    initialItem: Int,
    onItemSelected: (Int) -> Unit,
    label: String
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialItem)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val coroutineScope = rememberCoroutineScope()

    // This derived state will update whenever the scrolling stops
    val centeredItemIndex by remember {
        derivedStateOf {
            if (listState.isScrollInProgress) {
                // Return a placeholder while scrolling to avoid rapid changes
                -1
            } else {
                listState.firstVisibleItemIndex
            }
        }
    }

    // Update the parent's state when scrolling stops
    LaunchedEffect(centeredItemIndex) {
        if (centeredItemIndex != -1) {
            onItemSelected(items[centeredItemIndex])
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.height(120.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items.size) { index ->
                val itemValue = items[index]
                val isSelected = (index == centeredItemIndex || (index == initialItem && centeredItemIndex == -1))

                Text(
                    text = itemValue.toString().padStart(2, '0'),
                    style = if (isSelected) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
@Preview()
fun TimerPickerDialogPreview() {
    MaterialTheme {
        TimerPickerDialog(
            initialTimerInfo = TimerInfo(durationSeconds = 3661), // 1h 1m 1s
            onDismiss = {},
            onDelete = {},
            onSave = { h, m, s -> println("Saved: $h:$m:$s") }
        )
    }
}
