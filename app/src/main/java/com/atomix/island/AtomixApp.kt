package com.atomix.island

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AtomixApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val islandChannel = NotificationChannel(
                CHANNEL_ISLAND_SERVICE,
                "Atomix Island Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps Atomix Island running"
                setShowBadge(false)
            }

            val mediaChannel = NotificationChannel(
                CHANNEL_MEDIA,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Media controls"
                setShowBadge(false)
            }

            val callChannel = NotificationChannel(
                CHANNEL_CALLS,
                "Calls",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Incoming call notifications"
            }

            manager.createNotificationChannels(listOf(islandChannel, mediaChannel, callChannel))
        }
    }

    companion object {
        const val CHANNEL_ISLAND_SERVICE = "atomix_island_service"
        const val CHANNEL_MEDIA = "atomix_media"
        const val CHANNEL_CALLS = "atomix_calls"
    }
}
