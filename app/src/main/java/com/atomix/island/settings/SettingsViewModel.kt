package com.atomix.island.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atomix.island.ui.components.IslandEvent
import com.atomix.island.ui.components.IslandState
import com.atomix.island.utils.IslandPreferences
import com.atomix.island.utils.IslandPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: IslandPreferencesDataStore
) : ViewModel() {

    val preferences: StateFlow<IslandPreferences> = prefs.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), IslandPreferences())

    // Live preview state
    private val _previewState = MutableStateFlow<IslandState>(IslandState.Compact)
    val previewState: StateFlow<IslandState> = _previewState.asStateFlow()

    fun setPreviewState(state: IslandState) {
        _previewState.value = state
    }

    fun updateAccentColor(colorLong: Long) {
        viewModelScope.launch { prefs.setAccentColor(colorLong) }
    }

    fun updateGlowIntensity(v: Float) {
        viewModelScope.launch { prefs.setGlowIntensity(v) }
    }

    fun updateAnimationSpeed(v: Float) {
        viewModelScope.launch { prefs.setAnimationSpeed(v) }
    }

    fun updateIslandSize(width: Float, height: Float) {
        viewModelScope.launch {
            prefs.update {
                this[com.atomix.island.utils.IslandPreferencesDataStore.Keys.ISLAND_WIDTH]  = width
                this[com.atomix.island.utils.IslandPreferencesDataStore.Keys.ISLAND_HEIGHT] = height
            }
        }
    }

    fun updateCornerRadius(v: Float) {
        viewModelScope.launch {
            prefs.update {
                this[com.atomix.island.utils.IslandPreferencesDataStore.Keys.CORNER_RADIUS] = v
            }
        }
    }

    fun updateAutoHide(enabled: Boolean) {
        viewModelScope.launch {
            prefs.update {
                this[com.atomix.island.utils.IslandPreferencesDataStore.Keys.AUTO_HIDE] = enabled
            }
        }
    }

    // Preview shortcut events
    fun previewMusic() {
        _previewState.value = IslandState.Expanded(
            IslandEvent.Music("Starboy", "The Weeknd", progress = 0.4f, isPlaying = true)
        )
    }

    fun previewCall() {
        _previewState.value = IslandState.FullIsland(
            IslandEvent.IncomingCall("Aria Stark", "+1 234 567 8901")
        )
    }

    fun previewAssistant() {
        _previewState.value = IslandState.Expanded(
            IslandEvent.VoiceAssistant(
                com.atomix.island.ui.components.AssistantState.LISTENING, "Gemini"
            )
        )
    }

    fun previewCompact() {
        _previewState.value = IslandState.Compact
    }
}
