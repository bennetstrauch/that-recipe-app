package com.plcoding.bookpedia.recipe.presentation.util

//##have timer keep track of state when going to different recipe?
import com.plcoding.bookpedia.recipe.domain.TimerInfo

fun Int.minutesToTimerInfo(): TimerInfo = TimerInfo(durationSeconds = this * 60L)

enum class Timer { DISPLAY_1_MINUTE_OFFSET }

fun formatDuration(totalSeconds: Long, displayOffset: Timer? = null): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    var offset = 0
    if (displayOffset == Timer.DISPLAY_1_MINUTE_OFFSET) {
        offset = 1
    }

    return if (hours > 0) {
        "$hours"+"h:${(minutes+offset).toString().padStart(2, '0')}"
    } else if (minutes > 10) {
//
        "${minutes+offset} min"
    }
    else {
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}

//#implement in UI but be careful with offset
fun TimerInfo.toFormattedString(): String = formatDuration(this.durationSeconds)

fun toTotalSeconds(hours: Int, minutes: Int, seconds: Int): Long {
    return hours * 3600L + minutes * 60L + seconds
}
