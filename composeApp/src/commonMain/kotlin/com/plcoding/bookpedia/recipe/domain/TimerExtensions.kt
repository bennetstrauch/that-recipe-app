package com.plcoding.bookpedia.recipe.domain

fun Int.minutesToTimerInfo(): TimerInfo = TimerInfo(durationSeconds = this * 60L)
