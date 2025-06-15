package com.plcoding.bookpedia.recipe.presentation.recipe_edit.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.plcoding.bookpedia.recipe.domain.TimerInfo
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TimerPickerDialogInteractive(
    initialTimerInfo: TimerInfo?,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: (hours: String, minutes: String, seconds: String) -> Unit
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
                            selectedHour.toString(),
                            selectedMinute.toString(),
                            selectedSecond.toString()
                        )
                    }) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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
    val focusManager = LocalFocusManager.current

    var isEditing by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf(initialItem.toString()) }

    // This derived state will update whenever the scrolling stops
    val centeredItemIndex by remember {
        derivedStateOf {
            if (listState.isScrollInProgress) {
                -1 // Return a placeholder while scrolling
            } else {
                listState.firstVisibleItemIndex
            }
        }
    }

    // Update the parent's state when scrolling stops
    LaunchedEffect(centeredItemIndex) {
        if (centeredItemIndex != -1) {
            val selectedValue = items[centeredItemIndex]
            textValue = selectedValue.toString()
            onItemSelected(selectedValue)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(64.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall)
        Box(
            modifier = Modifier.height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isEditing) {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            val newValue = textValue.toIntOrNull()?.coerceIn(items.first(), items.last()) ?: initialItem
                            onItemSelected(newValue)
                            coroutineScope.launch {
                                listState.scrollToItem(items.indexOf(newValue).coerceAtLeast(0))
                            }
                            isEditing = false
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier
                        .height(60.dp)
                        .onFocusChanged { focusState ->
                            if (!focusState.isFocused) {
                                isEditing = false
                            }
                        }
                )
            } else {
                LazyColumn(
                    state = listState,
                    flingBehavior = flingBehavior,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Add empty items at the top and bottom to allow centering the first and last items
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                    items(items.size) { index ->
                        val itemValue = items[index]
                        Text(
                            text = itemValue.toString().padStart(2, '0'),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .clickable { isEditing = true }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(40.dp)) }
                }
            }
        }
    }
}


@Composable
@Preview()
fun TimerPickerDialogInteractivePreview() {
    MaterialTheme {
        TimerPickerDialogInteractive(
            initialTimerInfo = TimerInfo(durationSeconds = 3661), // 1h 1m 1s
            onDismiss = {},
            onDelete = {},
            onSave = { h, m, s -> println("Saved: $h:$m:$s") }
        )
    }
}
