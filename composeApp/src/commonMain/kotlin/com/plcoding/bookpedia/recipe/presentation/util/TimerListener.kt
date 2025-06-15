package com.plcoding.bookpedia.recipe.presentation.util

interface TimerListener {
    fun onTick(stepId: String, remainingSeconds: Long)
    fun onFinish(stepId: String)
}
