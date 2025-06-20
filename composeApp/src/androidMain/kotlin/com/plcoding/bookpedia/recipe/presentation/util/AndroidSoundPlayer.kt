package com.plcoding.bookpedia.recipe.presentation.util

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import kotlinx.coroutines.delay

/**
 * The Android-specific implementation of the SoundPlayer interface.
 * It takes a Context in its constructor to perform the platform-specific work.
 * Note: This is a regular class, not marked with 'actual'.
 */
class AndroidSoundPlayer(private val context: Context) : SoundPlayer {

    override fun playSound() {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(context.applicationContext, notification)
//            ##refine with start and stop add other implementations
            r.play()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}