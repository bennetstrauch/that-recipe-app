package com.plcoding.bookpedia.recipe.presentation.recipe_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.plcoding.bookpedia.app.RecipeDetail
import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.core.domain.onError
import com.plcoding.bookpedia.core.domain.onSuccess
import com.plcoding.bookpedia.core.presentation.toUiText
import com.plcoding.bookpedia.recipe.domain.GetRecipeDetailsUseCase
import com.plcoding.bookpedia.recipe.domain.RecipeRepository
import com.plcoding.bookpedia.recipe.domain.TimerInfo
import com.plcoding.bookpedia.recipe.presentation.util.SoundPlayer
import com.plcoding.bookpedia.recipe.presentation.util.TimerListener
import com.plcoding.bookpedia.recipe.presentation.util.TimerManager
import com.plcoding.bookpedia.recipe.presentation.util.minutesToTimerInfo
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val getRecipeDetailsUseCase: GetRecipeDetailsUseCase,
    private val savedStateHandle: SavedStateHandle,
    private val soundPlayer: SoundPlayer
) : ViewModel(), TimerListener {

    private val timerManager = TimerManager(viewModelScope)
    // A map to hold active timer jobs, so they can be cancelled.
//    private val timerJobs = mutableMapOf<String, Job>()
    private val getPrepTime = { state.value.selectedVersion?.overridePrepTimeMinutes ?: state.value.recipeHeader?.defaultPrepTimeMinutes ?: 0 }

    companion object {
        const val PREP_TIMER_STEP_ID = "global_prep_timer"
    }


    private val _state = MutableStateFlow(RecipeDetailState())
    val state = _state.asStateFlow()
    // EventState:
    private val _eventFlow = MutableSharedFlow<RecipeDetailEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

//    ?#
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
            is RecipeDetailAction.OnTogglePictureVisibility -> {
                _state.update { it.copy(isPictureVisible = !it.isPictureVisible) }
            }
            is RecipeDetailAction.OnToggleMetaInfo -> {
                _state.update { it.copy(isMetaInfoExpanded = !it.isMetaInfoExpanded) }
            }
            is RecipeDetailAction.OnTimerClick -> handleTimer(action.stepId)
            is RecipeDetailAction.OnPauseTimer -> pauseTimer(action.stepId)
            is RecipeDetailAction.OnResumeTimer -> resumeTimer(action.stepId)
//            ##extract
            is RecipeDetailAction.OnFavoriteClick -> {
                viewModelScope.launch {
                    state.value.recipeHeader?.let {
                        if(it.isFavorite) {
                            recipeRepository.removeFromFavorites(headerId)
                        } else {
                            recipeRepository.markAsFavorite(headerId)
                        }
                    }
                }
            }
            else -> Unit
        }
    }

//    #unify?
private fun observeRecipeDetails() {
    viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }

        getRecipeDetailsUseCase(headerId, state.value.selectedVersion?.id)
            .collect { result ->
                var newState = _state.value.copy(isLoading = false)

                result
                    .onSuccess { details ->
                        if (details != null) {
                            newState = newState.copy(
                                recipeHeader = details.header,
                                allVersions = details.allVersions,
                                selectedVersion = details.selectedVersion
                            )
                        } else {
//                            maybe better / different error handling here: #
                            newState = newState.copy(errorMessage = DataError.Local.NO_RECIPE_FOUND.toUiText())
                        }
                    }
                    .onError { error ->
                        newState = newState.copy(errorMessage = error.toUiText())
                    }

                _state.value = newState
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
//            ### do i want this? if not, when to cancel?
            // Cancel all running timers from the previous version
//            timerJobs.values.forEach { it.cancel() }
//            timerJobs.clear()
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
        if (_state.value.runningTimers.containsKey(stepId)) {
            timerManager.cancelTimer(stepId)
            _state.update {
                it.copy(runningTimers = it.runningTimers - stepId)
            }
        } else {
            val timerInfo : TimerInfo
            if(stepId == PREP_TIMER_STEP_ID){
//            ## remove nullable from headerField?
                val prepTime = state.value.selectedVersion?.overridePrepTimeMinutes ?: state.value.recipeHeader?.defaultPrepTimeMinutes ?: 0
                timerInfo = prepTime.minutesToTimerInfo()
            }else {
                val step = _state.value.selectedVersion?.directions?.find { it.id == stepId }
                timerInfo = step?.timerInfo ?: return // No timer for this step
            }

            timerManager.startTimer(stepId, timerInfo, this)
        }
    }


    fun pauseTimer(stepId: String) {
        timerManager.pauseTimer(stepId)
        _state.update {
            it.copy(pausedTimers = it.pausedTimers + stepId)
        }
    }

    fun resumeTimer(stepId: String) {
        timerManager.resumeTimer(stepId)
        _state.update {
            it.copy(pausedTimers = it.pausedTimers - stepId)
        }
    }


//    #object difference??
    override fun onTick(stepId: String, remainingSeconds: Long) {
        _state.update {
            it.copy(runningTimers = it.runningTimers + (stepId to remainingSeconds))
        }
    }

    override fun onFinish(stepId: String) {
        _state.update {
            it.copy(
                runningTimers = it.runningTimers - stepId,
                checkedStepIds = it.checkedStepIds + stepId
            )
        }
        playAlarm()
    }


    private fun playAlarm() {
        // Platform-specific solution
        viewModelScope.launch {
            _eventFlow.emit(RecipeDetailEvent.PlayAlarmSound)
        }
    }
}
