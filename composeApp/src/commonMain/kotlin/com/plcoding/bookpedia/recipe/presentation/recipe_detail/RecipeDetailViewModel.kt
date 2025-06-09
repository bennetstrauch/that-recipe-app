package com.plcoding.bookpedia.recipe.presentation.recipe_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // A map to hold active timer jobs, so they can be cancelled.
    private val timerJobs = mutableMapOf<String, Job>()

    private val _state = MutableStateFlow(RecipeDetailState())
    val state = _state.asStateFlow()

    init {
        // Fetch the recipe header ID passed during navigation
        val recipeHeaderId = savedStateHandle.get<String>("recipeHeaderId")
        if (recipeHeaderId == null) {
            // Handle error: No ID provided
            _state.update { it.copy(isLoading = false) }
        } else {
            loadRecipeDetails(recipeHeaderId)
        }
    }

    fun onAction(action: RecipeDetailAction) {
        when (action) {
            is RecipeDetailAction.OnSelectVersion -> selectVersion(action.versionId)
            is RecipeDetailAction.OnToggleIngredientCheck -> toggleIngredient(action.ingredientId)
            is RecipeDetailAction.OnToggleStepCheck -> toggleStep(action.stepId)
            is RecipeDetailAction.OnTimerClick -> handleTimer(action.stepId)
            is RecipeDetailAction.OnTogglePictureVisibility -> {
                _state.update { it.copy(isPictureVisible = !it.isPictureVisible) }
            }
            else -> Unit
        }
    }

    private fun loadRecipeDetails(headerId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Fetch both header and all its versions from the repository
            val header = recipeRepository.getRecipeHeaderById(headerId)
            val versions = recipeRepository.getVersionsForRecipe(headerId)

            _state.update {
                it.copy(
                    isLoading = false,
                    recipeHeader = header,
                    allVersions = versions,
                    // Select the first version by default (or the one marked as "Original")
                    selectedVersion = versions.firstOrNull()
                )
            }
        }
    }

    private fun selectVersion(versionId: String) {
        val newVersion = _state.value.allVersions.find { it.id == versionId }
        if (newVersion != null) {
            _state.update {
                it.copy(
                    selectedVersion = newVersion,
                    // Reset checked items and timers when version changes
                    checkedIngredientIds = emptySet(),
                    checkedStepIds = emptySet(),
                    runningTimers = emptyMap()
                )
            }
            // Cancel all running timers from the previous version
            timerJobs.values.forEach { it.cancel() }
            timerJobs.clear()
        }
    }

    private fun toggleIngredient(ingredientId: String) {
        val currentIds = _state.value.checkedIngredientIds.toMutableSet()
        if (ingredientId in currentIds) {
            currentIds.remove(ingredientId)
        } else {
            currentIds.add(ingredientId)
        }
        _state.update { it.copy(checkedIngredientIds = currentIds) }
    }

    private fun toggleStep(stepId: String) {
        val currentIds = _state.value.checkedStepIds.toMutableSet()
        if (stepId in currentIds) {
            currentIds.remove(stepId)
        } else {
            currentIds.add(stepId)
        }
        _state.update { it.copy(checkedStepIds = currentIds) }
    }

    private fun handleTimer(stepId: String) {
        val step = _state.value.selectedVersion?.directions?.find { it.id == stepId }
        val timerInfo = step?.timerInfo ?: return // No timer for this step

        if (timerJobs[stepId]?.isActive == true) {
            // If timer is already running, cancel it
            timerJobs[stepId]?.cancel()
            timerJobs.remove(stepId)
            _state.update { it.copy(runningTimers = it.runningTimers - stepId) }
        } else {
            // Start a new timer
            timerJobs[stepId] = viewModelScope.launch {
                var remainingSeconds = timerInfo.durationSeconds
                while (remainingSeconds > 0) {
                    _state.update {
                        it.copy(runningTimers = it.runningTimers + (stepId to remainingSeconds))
                    }
                    delay(1000L)
                    remainingSeconds--
                }
                // Timer finished
                _state.update {
                    it.copy(
                        runningTimers = it.runningTimers - stepId,
                        // Automatically check the step once the timer is done
                        checkedStepIds = it.checkedStepIds + stepId
                    )
                }
                timerJobs.remove(stepId)
            }
        }
    }
}
