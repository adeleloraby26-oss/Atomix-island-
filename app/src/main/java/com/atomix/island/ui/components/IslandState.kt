package com.atomix.island.ui.components

import androidx.compose.ui.graphics.Color
import com.atomix.island.ui.theme.AtomixColors

// ─── Island Display States ───────────────────────────────────────────────────
sealed class IslandState {
    /** Minimal pill — just a camera indicator */
    object Compact : IslandState()

    /** Slightly wider pill with a single icon + label */
    data class Mini(val event: IslandEvent) : IslandState()

    /** Standard expanded bar for music, calls, etc. */
    data class Expanded(val event: IslandEvent) : IslandState()

    /** Full-height card */
    data class FullIsland(val event: IslandEvent) : IslandState()

    /** Two events side-by-side */
    data class SplitIsland(val left: IslandEvent, val right: IslandEvent) : IslandState()

    /** Dual floating pills */
    data class DualIsland(val primary: IslandEvent, val secondary: IslandEvent) : IslandState()
}

// ─── Event Types ─────────────────────────────────────────────────────────────
sealed class IslandEvent {

    // ── Music ────────────────────────────────────────────────────────────
    data class Music(
        val title: String,
        val artist: String,
        val albumArt: String? = null,
        val isPlaying: Boolean = true,
        val progress: Float = 0f,
        val duration: Long = 0L,
        val isFavorite: Boolean = false,
    ) : IslandEvent()

    // ── Calls ────────────────────────────────────────────────────────────
    data class IncomingCall(
        val callerName: String,
        val callerNumber: String,
        val callerPhoto: String? = null,
        val appPackage: String = "",
    ) : IslandEvent()

    data class ActiveCall(
        val callerName: String,
        val duration: Long = 0L,
        val isMuted: Boolean = false,
        val isOnSpeaker: Boolean = false,
    ) : IslandEvent()

    data class MissedCall(
        val callerName: String,
        val count: Int = 1,
    ) : IslandEvent()

    // ── Notifications ────────────────────────────────────────────────────
    data class Notification(
        val appName: String,
        val appPackage: String,
        val title: String,
        val text: String,
        val iconRes: Int? = null,
        val avatarUrl: String? = null,
        val priority: NotificationPriority = NotificationPriority.NORMAL,
        val accentColor: Color = AtomixColors.ElectricBlue,
    ) : IslandEvent()

    // ── System ───────────────────────────────────────────────────────────
    data class BatteryCharging(val level: Int, val isCharging: Boolean) : IslandEvent()
    data class BatteryLow(val level: Int) : IslandEvent()
    data class BatteryFull(val level: Int = 100) : IslandEvent()
    data class VolumeChange(val level: Int, val maxLevel: Int, val streamType: StreamType) : IslandEvent()
    data class BrightnessChange(val level: Int) : IslandEvent()

    // ── Connectivity ─────────────────────────────────────────────────────
    data class WifiChange(val ssid: String, val isConnected: Boolean, val signalStrength: Int = 3) : IslandEvent()
    data class BluetoothDevice(val name: String, val isConnected: Boolean, val batteryLevel: Int? = null) : IslandEvent()
    data class HeadphonesConnected(val name: String = "Headphones", val isWireless: Boolean = false) : IslandEvent()

    // ── Media & Screen ────────────────────────────────────────────────────
    object Screenshot : IslandEvent()
    object ScreenRecordingStart : IslandEvent()
    object ScreenRecordingStop : IslandEvent()

    // ── Download ─────────────────────────────────────────────────────────
    data class Download(val fileName: String, val progress: Float, val isComplete: Boolean = false) : IslandEvent()

    // ── Voice Assistants ──────────────────────────────────────────────────
    data class VoiceAssistant(
        val state: AssistantState = AssistantState.LISTENING,
        val assistantName: String = "Assistant",
        val responseText: String = "",
    ) : IslandEvent()

    // ── Sensors / Hardware ────────────────────────────────────────────────
    object CameraActive : IslandEvent()
    object MicrophoneActive : IslandEvent()
    data class AlarmFiring(val label: String = "Alarm", val time: String = "") : IslandEvent()
    data class TimerFinished(val label: String = "Timer") : IslandEvent()

    // ── Navigation ────────────────────────────────────────────────────────
    data class Navigation(
        val instruction: String,
        val distance: String,
        val eta: String = "",
        val app: String = "Maps",
    ) : IslandEvent()

    // ── App Events ────────────────────────────────────────────────────────
    data class AppInstall(val appName: String, val progress: Float = 1f) : IslandEvent()
    data class AppUpdate(val appName: String, val count: Int = 1) : IslandEvent()

    // ── Idle ──────────────────────────────────────────────────────────────
    object Idle : IslandEvent()
}

// ─── Supporting Enums ────────────────────────────────────────────────────────
enum class NotificationPriority { LOW, NORMAL, HIGH, URGENT }
enum class AssistantState { IDLE, LISTENING, THINKING, RESPONDING }
enum class StreamType { MUSIC, RING, NOTIFICATION, SYSTEM, CALL, ALARM }
