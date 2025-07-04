package com.plcoding.bookpedia.recipe.presentation.recipe_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.material3.DividerDefaults.color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.plcoding.bookpedia.core.presentation.Brown
import com.plcoding.bookpedia.core.presentation.DarkGreen
import com.plcoding.bookpedia.core.presentation.LightGreen
import com.plcoding.bookpedia.core.presentation.MediumGreen
import com.plcoding.bookpedia.core.presentation.MediumTurquoise
import com.plcoding.bookpedia.core.presentation.Orange
import com.plcoding.bookpedia.core.presentation.SandYellow
import com.plcoding.bookpedia.core.presentation.Skin
import com.plcoding.bookpedia.recipe.domain.InstructionStep
import com.plcoding.bookpedia.recipe.domain.RecipeVersion
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.components.DirectionStepItem
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.components.IngredientItem
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.components.MetaInfoSection
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.components.TimerItem
import com.plcoding.bookpedia.recipe.presentation.util.SoundPlayer
import kotlinx.coroutines.delay
import org.koin.compose.koinInject

// Stateful Root Composable
@Composable
fun RecipeDetailScreenRoot(
    viewModel: RecipeDetailViewModel,
    onBackClick: () -> Unit,
    onEditClick: (recipeHeaderId: String, recipeVersionId: String) -> Unit,
    prepTimerStepId: String = RecipeDetailViewModel.PREP_TIMER_STEP_ID,
    soundPlayer: SoundPlayer = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // 2. Event-Listener:
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is RecipeDetailEvent.PlayAlarmSound -> {
                    // When the ViewModel sends the event, the UI reacts and plays the sound.
                    soundPlayer.playSound()
                    delay(800)
                    soundPlayer.playSound()
                    delay(800)
                    soundPlayer.playSound()
                }

            }
        }
    }

    RecipeDetailScreen(
        state = state,
        prepTimerStepId = prepTimerStepId,
        onAction = { action ->
            when (action) {
                is RecipeDetailAction.OnBackClick -> onBackClick()
                is RecipeDetailAction.OnEditClick -> onEditClick(state.recipeHeader!!.id, state.selectedVersion!!.id)
                else -> viewModel.onAction(action)
            }
        }
    )
}

// Stateless Screen Composable for UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailScreen(
    state: RecipeDetailState,
    prepTimerStepId: String,
    onAction: (RecipeDetailAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MediumGreen.copy(0.3f)
                ),

                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(state.recipeHeader?.title ?: "Loading...")
                        IconButton(
                            onClick = { onAction(RecipeDetailAction.OnFavoriteClick) },
                            modifier = Modifier.padding(start = 8.dp)

                        ) {
                            Icon(
                                imageVector = if (state.recipeHeader?.isFavorite == true) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Toggle Favorite"
                            )
                        }
                    }
                },


                navigationIcon = {
                    IconButton(onClick = { onAction(RecipeDetailAction.OnBackClick) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    val prepTime = state.selectedVersion?.overridePrepTimeMinutes ?: state.recipeHeader?.defaultPrepTimeMinutes
//                    ##should i make prepTime a non nullable?
                    if (prepTime != null) {
                        TimerItem(
                            isRunning = state.runningTimers.containsKey(prepTimerStepId),
                            isPaused = state.pausedTimers.contains(prepTimerStepId),
//                            #do i need this remaining seconds or can i refactor?
                            remainingSeconds = state.runningTimers[prepTimerStepId] ?: 0L,
                            // The text to display when the timer is NOT running
                            staticText = "${state.selectedVersion?.overridePrepTimeMinutes ?: state.recipeHeader?.defaultPrepTimeMinutes ?: 0} min",
                            onClick = { onAction(RecipeDetailAction.OnTimerClick(prepTimerStepId)) },
                            onPause = { onAction(RecipeDetailAction.OnPauseTimer(prepTimerStepId)) },
                            onResume = {
                                onAction(
                                    RecipeDetailAction.OnResumeTimer(
                                        prepTimerStepId
                                    )
                                )
                            }
                        )
                    }

                    VersionDropdown(
                        versions = state.allVersions,
                        selectedVersionId = state.selectedVersion?.id,
                        onAction = onAction
                    )
                    // Edit Button
                    IconButton(onClick = { onAction(RecipeDetailAction.OnEditClick) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Recipe")
                    }
                }
            )
        },
        bottomBar = {
            if (state.recipeHeader != null && state.selectedVersion != null) {
                MetaInfoSection(
                    categoryName = state.recipeHeader.category.name,
                    versionCommentary = state.selectedVersion.versionCommentary,
                    isExpanded = state.isMetaInfoExpanded,
                    onToggle = { onAction(RecipeDetailAction.OnToggleMetaInfo) }
                )
            }
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.recipeHeader == null || state.selectedVersion == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                // Display error message from state if it exists
                Text(state.errorMessage?.asString() ?: "Recipe not found.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // --- Picture Section can be added here ---
                // item {
                //     AsyncImage(
                //         model = state.recipeHeader.imageUrl,
                //         contentDescription = state.recipeHeader.title,
                //         modifier = Modifier.fillMaxWidth().height(200.dp),
                //         contentScale = ContentScale.Crop
                //     )
                // }

                // --- Ingredients Section ---

                item {
                    Card(modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor= Skin.copy(0.2f)),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                "Ingredients",
                                style = MaterialTheme.typography.titleLarge,
                                color = MediumTurquoise
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Use a simple Column for ingredients as they are part of the 'item' scope
                            // and not directly within LazyColumn's items block here.
                            Column {
                                state.selectedVersion.ingredients.forEach { ingredient ->
                                    IngredientItem(
                                        ingredient = ingredient,
                                        isChecked = ingredient.id in state.checkedIngredientIds,
                                        onCheckedChange = {
                                            onAction(RecipeDetailAction.OnToggleIngredientCheck(ingredient.id))
                                        }
                                    )
                                }
                            }
                         }
                    }
                }



                // --- Directions Section ---
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Directions", style = MaterialTheme.typography.titleLarge)
                }
                items(state.selectedVersion.directions) { step ->
                    DirectionStepItem(
                        step = step,
                        isRunning = state.runningTimers.containsKey(step.id),
                        isPaused = state.pausedTimers.contains(step.id),
                        remainingSeconds = state.runningTimers[step.id],
                        isChecked = step.id in state.checkedStepIds,
                        onAction = onAction
                    )
                }

            }
        }
    }
}


@Composable
private fun VersionDropdown(
    versions: List<RecipeVersion>,
    selectedVersionId: String?,
    onAction: (RecipeDetailAction) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextButton(onClick = { expanded = true }) {
            Text(versions.find { it.id == selectedVersionId }?.versionName ?: "Version")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            versions.forEach { version ->
                DropdownMenuItem(
                    text = { Text(version.versionName) },
                    onClick = {
                        onAction(RecipeDetailAction.OnSelectVersion(version.id))
                        expanded = false
                    }
                )
            }
        }
    }
}


