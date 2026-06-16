package com.atomix.island.utils

import android.app.ActivityManager
import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.BatteryManager
import androidx.annotation.RequiresApi
import com.atomix.island.ui.components.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Central hub that reads Android system APIs and converts to IslandEvents.
 * Emits a continuous stream of events for the ViewModel to consume.
 */
@Singleton
class IslandEventMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val _events = MutableSharedFlow<IslandEvent>(extraBufferCapacity = 32)
    val events = _events.asSharedFlow()

    // ── Battery ───────────────────────────────────────────────────────────────
    fun emitBatteryEvent(level: Int, isCharging: Boolean, isFull: Boolean) {
        scope.launch {
            val event = when {
                isFull      -> IslandEvent.BatteryFull(level)
                level <= 15 -> IslandEvent.BatteryLow(level)
                else        -> IslandEvent.BatteryCharging(level, isCharging)
            }
            _events.emit(event)
        }
    }

    // ── Volume ────────────────────────────────────────────────────────────────
    fun emitVolumeEvent(level: Int, max: Int, stream: StreamType) {
        scope.launch { _events.emit(IslandEvent.VolumeChange(level, max, stream)) }
    }

    // ── Network ───────────────────────────────────────────────────────────────
    fun emitWifiEvent(ssid: String, connected: Boolean, signal: Int = 3) {
        scope.launch { _events.emit(IslandEvent.WifiChange(ssid, connected, signal)) }
    }

    fun emitBluetoothEvent(name: String, connected: Boolean, battery: Int? = null) {
        scope.launch { _events.emit(IslandEvent.BluetoothDevice(name, connected, battery)) }
    }

    // ── Media ────────────────────────────────────────────────────────────────
    fun emitScreenshotEvent() {
        scope.launch { _events.emit(IslandEvent.Screenshot) }
    }

    fun emitScreenRecordStart() {
        scope.launch { _events.emit(IslandEvent.ScreenRecordingStart) }
    }

    // ── Downloads ────────────────────────────────────────────────────────────
    fun emitDownloadProgress(fileName: String, progress: Float, complete: Boolean = false) {
        scope.launch { _events.emit(IslandEvent.Download(fileName, progress, complete)) }
    }

    // ── Navigation ───────────────────────────────────────────────────────────
    fun emitNavigationUpdate(instruction: String, distance: String, eta: String = "", app: String = "Maps") {
        scope.launch { _events.emit(IslandEvent.Navigation(instruction, distance, eta, app)) }
    }

    // ── Voice Assistant ───────────────────────────────────────────────────────
    fun emitAssistantState(state: AssistantState, name: String = "Assistant", text: String = "") {
        scope.launch { _events.emit(IslandEvent.VoiceAssistant(state, name, text)) }
    }

    // ── Camera / Mic ─────────────────────────────────────────────────────────
    @RequiresApi(Build.VERSION_CODES.S)
    fun monitorCameraAccess() {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraManager.registerAvailabilityCallback(
            object : CameraManager.AvailabilityCallback() {
                override fun onCameraUnavailable(cameraId: String) {
                    scope.launch { _events.emit(IslandEvent.CameraActive) }
                }
                override fun onCameraAvailable(cameraId: String) {
                    // Camera released
                }
            },
            null
        )
    }

    // ── RAM / CPU Stats ───────────────────────────────────────────────────────
    fun getMemoryInfo(): Pair<Float, Float> {
        val am   = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = ActivityManager.MemoryInfo()
        am.getMemoryInfo(info)
        val totalRam = info.totalMem.toFloat()
        val usedRam  = (info.totalMem - info.availMem).toFloat()
        return Pair(usedRam / totalRam, totalRam)
    }

    fun getCpuTemperature(): Float {
        return try {
            val result = Runtime.getRuntime().exec("cat /sys/class/thermal/thermal_zone0/temp")
            val output = result.inputStream.bufferedReader().readLine()
            (output?.toFloatOrNull() ?: 35000f) / 1000f
        } catch (e: Exception) {
            38f
        }
    }
}
