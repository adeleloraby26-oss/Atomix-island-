package com.atomix.island.services

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.*
import android.os.Build
import com.atomix.island.ui.components.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AtomixMediaService {

    companion object {
        private val _mediaState = MutableStateFlow<IslandEvent.Music?>(null)
        val mediaState = _mediaState.asStateFlow()
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

        fun attachMediaController(context: Context) {
            try {
                val sessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE)
                        as MediaSessionManager
                val component = ComponentName(context, AtomixNotificationService::class.java)
                val sessions  = sessionManager.getActiveSessions(component)
                sessions.firstOrNull()?.let { bindToSession(it) }

                sessionManager.addOnActiveSessionsChangedListener({ newSessions ->
                    newSessions?.firstOrNull()?.let { bindToSession(it) }
                }, component)
            } catch (e: SecurityException) {
                // Notification access not granted
            }
        }

        private fun bindToSession(controller: MediaController) {
            controller.registerCallback(object : MediaController.Callback() {
                override fun onMetadataChanged(metadata: MediaMetadata?) {
                    metadata ?: return
                    val isPlaying = controller.playbackState?.state ==
                            PlaybackState.STATE_PLAYING

                    val duration  = metadata.getLong(MediaMetadata.METADATA_KEY_DURATION)
                    val position  = controller.playbackState?.position ?: 0L
                    val progress  = if (duration > 0) position.toFloat() / duration else 0f

                    val music = IslandEvent.Music(
                        title    = metadata.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "Unknown",
                        artist   = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "Unknown",
                        isPlaying = isPlaying,
                        progress  = progress,
                        duration  = duration,
                    )

                    scope.launch {
                        _mediaState.value = music
                        IslandOverlayService.instance?.updateIslandState(
                            if (isPlaying) IslandState.Expanded(music)
                            else IslandState.Compact
                        )
                    }
                }

                override fun onPlaybackStateChanged(state: PlaybackState?) {
                    val currentMusic = _mediaState.value ?: return
                    val isPlaying = state?.state == PlaybackState.STATE_PLAYING
                    val updated = currentMusic.copy(isPlaying = isPlaying)
                    scope.launch {
                        _mediaState.value = updated
                        IslandOverlayService.instance?.updateIslandState(
                            if (isPlaying) IslandState.Expanded(updated)
                            else IslandState.Compact
                        )
                    }
                }
            })
        }
    }
}
