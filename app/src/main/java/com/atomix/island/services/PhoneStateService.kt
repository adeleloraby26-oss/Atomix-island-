package com.atomix.island.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.atomix.island.ui.components.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class PhoneStateService : Service() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private lateinit var telephonyManager: TelephonyManager
    private var callTimer: Job? = null
    private var callSeconds = 0L

    companion object {
        private val _callEventFlow = MutableSharedFlow<IslandEvent>(replay = 1)
        val callEventFlow = _callEventFlow.asSharedFlow()
    }

    private val phoneStateListener = object : PhoneStateListener() {
        @Deprecated("Deprecated in Java")
        override fun onCallStateChanged(state: Int, phoneNumber: String?) {
            scope.launch {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        val name = phoneNumber ?: "Unknown"
                        val event = IslandEvent.IncomingCall(
                            callerName   = name,
                            callerNumber = phoneNumber ?: ""
                        )
                        _callEventFlow.emit(event)
                        IslandOverlayService.instance?.updateIslandState(
                            IslandState.FullIsland(event)
                        )
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        callSeconds = 0L
                        callTimer?.cancel()
                        callTimer = scope.launch {
                            while (true) {
                                val event = IslandEvent.ActiveCall(
                                    callerName = phoneNumber ?: "Unknown",
                                    duration   = callSeconds++
                                )
                                _callEventFlow.emit(event)
                                IslandOverlayService.instance?.updateIslandState(
                                    IslandState.Expanded(event)
                                )
                                delay(1000)
                            }
                        }
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        callTimer?.cancel()
                        _callEventFlow.emit(IslandEvent.Idle)
                        IslandOverlayService.instance?.updateIslandState(IslandState.Compact)
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        @Suppress("DEPRECATION")
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }

    override fun onDestroy() {
        @Suppress("DEPRECATION")
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE)
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
