package com.plcoding.bookpedia.recipe.presentation.recipe_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.app.RecipeDetail
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import com.plcoding.bookpedia.recipe.domain.TimerInfo
import com.plcoding.bookpedia.recipe.domain.minutesToTimerInfo
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
    companion object {
        const val PREP_TIMER_STEP_ID = "global_prep_timer"
    }

    private val _state = MutableStateFlow(RecipeDetailState())
    val state = _state.asStateFlow()

    private val headerId: String

    init {
        // Get the headerId once from the navigation arguments
        val route = savedStateHandle.toRoute<RecipeDetail>()
        headerId = route.recipeHeaderId

        // Start observing the data from the database
        observeRecipeDetails()
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

//    #unify?
    private fun observeRecipeDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Create two flows: one for the header, one for all its versions
            val headerFlow = recipeRepository.getRecipeHeaderById(headerId)
            val versionsFlow = recipeRepository.getVersionsForRecipe(headerId)

            // Use `combine` to merge the latest emissions from both flows
            combine(headerFlow, versionsFlow) { headerResult, versionsResult ->
                // This block runs whenever either the header or the versions change in the DB

                var finalState = _state.value

                headerResult.onSuccess { header ->
                    finalState = finalState.copy(recipeHeader = header)
                }.onError { error ->
                    finalState = finalState.copy(errorMessage = error.toUiText())
                }

                versionsResult.onSuccess { versions ->
                    // If this is the first time loading, or the selected version was deleted,
                    // select the most recent one. Otherwise, keep the current selection.
                    val currentSelectedId = finalState.selectedVersion?.id
                    val newSelectedVersion =
                        versions.find { it.id == currentSelectedId } ?: versions.firstOrNull()

                    finalState = finalState.copy(
                        allVersions = versions,
                        selectedVersion = newSelectedVersion
                    )
                }.onError { error ->
                    finalState = finalState.copy(errorMessage = error.toUiText())
                }

                // Return the combined state, ensuring isLoading is turned off
                finalState.copy(isLoading = false)

            }.collect { combinedState ->
                // Update the UI with the final combined state
                _state.value = combinedState
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
        val timerInfo : TimerInfo
        if(stepId == PREP_TIMER_STEP_ID){
//            ## remove nullable from headerField?
            val prepTime = state.value.selectedVersion?.overridePrepTimeMinutes ?: state.value.recipeHeader?.defaultPrepTimeMinutes ?: 0
            timerInfo = prepTime.minutesToTimerInfo()
        }else {
            val step = _state.value.selectedVersion?.directions?.find { it.id == stepId }
            timerInfo = step?.timerInfo ?: return // No timer for this step
        }

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
                    if (stepId != PREP_TIMER_STEP_ID) {
                        it.copy(
                            // Automatically check the step once the timer is done
                            checkedStepIds = it.checkedStepIds + stepId,
                        )
                    }
                    it.copy( runningTimers = it.runningTimers - stepId, )
                }
                timerJobs.remove(stepId)
            }
        }
    }
}
