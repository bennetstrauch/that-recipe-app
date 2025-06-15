package com.plcoding.bookpedia.recipe.presentation.util

import com.plcoding.bookpedia.recipe.domain.TimerInfo
import com.plcoding.bookpedia.recipe.presentation.recipe_detail.RecipeDetailViewModel.Companion.PREP_TIMER_STEP_ID
import kotlinx.coroutines.*

class TimerManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {

    private val jobs = mutableMapOf<String, Job>()

    fun startTimer(
        stepId: String,
        info: TimerInfo,
        listener: TimerListener
    ) {
//        ?#
//        cancelTimer(stepId)


        jobs[stepId] = scope.launch {
            var remaining = info.durationSeconds

            while (remaining > 0) {
                delay(1000L)
                remaining--
                listener.onTick(stepId, remaining)
            }
            listener.onFinish(stepId)
        }

    }

    fun cancelTimer(stepId: String) {
        jobs[stepId]?.cancel()
        jobs.remove(stepId)
    }

    fun cancelAll() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
    }
}

