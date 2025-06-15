package com.plcoding.bookpedia.recipe.presentation.util

//##have timer keep track of state when going to different recipe?
import com.plcoding.bookpedia.recipe.domain.TimerInfo

fun Int.minutesToTimerInfo(): TimerInfo = TimerInfo(durationSeconds = this * 60L)


fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        "$hours"+"h:${(minutes+1).toString().padStart(2, '0')}"
    } else if (minutes > 10) {
//        ##+1
        "${minutes+1} min"
    }
    else {
        "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
    }
}

//#implement in UI
fun TimerInfo.toFormattedString(): String = formatDuration(this.durationSeconds)

fun toTotalSeconds(hours: Int, minutes: Int, seconds: Int): Long {
    return hours * 3600L + minutes * 60L + seconds
}
