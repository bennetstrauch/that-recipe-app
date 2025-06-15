package com.plcoding.bookpedia.recipe.presentation.util

import com.plcoding.bookpedia.recipe.domain.TimerInfo
import kotlinx.coroutines.*

class TimerManager(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private data class ActiveTimer(
//        flatmap timerInfo? #
        val timerInfo: TimerInfo,
        var remainingSeconds: Long,
        var isPaused: Boolean = false
    )

    private val jobs = mutableMapOf<String, Job>()
    private val activeTimers = mutableMapOf<String, ActiveTimer>()


    fun startTimer(
        stepId: String,
        info: TimerInfo,
        listener: TimerListener
    ) {
//        ?#
//        cancelTimer(stepId)
        val activeTimer = ActiveTimer(info, info.durationSeconds)
        activeTimers[stepId] = activeTimer


        jobs[stepId] = scope.launch {
//            #better not assign but use remaining seconds?
            var remainingSeconds = activeTimer.timerInfo.durationSeconds


            while (remainingSeconds > 0) {
                if(!activeTimer.isPaused) {
                    delay(1000L)
                    remainingSeconds--
                    listener.onTick(stepId, remainingSeconds)
                } else { delay(1000L) }
            }
            listener.onFinish(stepId)
            activeTimers.remove(stepId)
            jobs.remove(stepId)

        }

    }

    fun pauseTimer(stepId: String) {
        activeTimers[stepId]?.isPaused = true
    }

    fun resumeTimer(stepId: String) {
        activeTimers[stepId]?.isPaused = false
    }

    fun cancelTimer(stepId: String) {
        jobs[stepId]?.cancel()
        jobs.remove(stepId)
    }

    fun cancelAll() {
        jobs.values.forEach { it.cancel() }
        jobs.clear()
    }

//    fun isPaused(stepId: String): Boolean {
//        return activeTimers[stepId]?.isPaused ?: false
//    }
}

