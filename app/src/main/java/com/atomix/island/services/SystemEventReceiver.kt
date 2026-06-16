package com.atomix.island.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.BatteryManager
import com.atomix.island.ui.components.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SystemEventReceiver : BroadcastReceiver() {

    companion object {
        private val _eventFlow = MutableSharedFlow<IslandEvent>(replay = 1)
        val eventFlow = _eventFlow.asSharedFlow()
        private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    }

    override fun onReceive(context: Context, intent: Intent) {
        val event: IslandEvent? = when (intent.action) {
            Intent.ACTION_BATTERY_LOW -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                IslandEvent.BatteryLow(level)
            }
            Intent.ACTION_POWER_CONNECTED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100)
                IslandEvent.BatteryCharging(level, isCharging = true)
            }
            Intent.ACTION_POWER_DISCONNECTED -> {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100)
                IslandEvent.BatteryCharging(level, isCharging = false)
            }
            "android.media.VOLUME_CHANGED_ACTION" -> {
                val streamType = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", 3)
                val level      = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", 0)
                val maxLevel   = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE", 15)
                val st = when (streamType) {
                    AudioManager.STREAM_MUSIC        -> StreamType.MUSIC
                    AudioManager.STREAM_RING         -> StreamType.RING
                    AudioManager.STREAM_NOTIFICATION -> StreamType.NOTIFICATION
                    AudioManager.STREAM_VOICE_CALL   -> StreamType.CALL
                    AudioManager.STREAM_ALARM        -> StreamType.ALARM
                    else                             -> StreamType.SYSTEM
                }
                IslandEvent.VolumeChange(level, maxLevel, st)
            }
            Intent.ACTION_HEADSET_PLUG -> {
                val state = intent.getIntExtra("state", 0)
                if (state == 1) IslandEvent.HeadphonesConnected() else null
            }
            else -> null
        }

        event?.let { e ->
            scope.launch {
                _eventFlow.emit(e)
                IslandOverlayService.instance?.updateIslandState(IslandState.Expanded(e))
                delay(3500)
                IslandOverlayService.instance?.updateIslandState(IslandState.Compact)
            }
        }
    }
}
