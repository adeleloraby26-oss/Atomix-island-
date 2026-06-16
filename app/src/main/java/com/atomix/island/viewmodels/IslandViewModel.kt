package com.atomix.island.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atomix.island.services.AtomixMediaService
import com.atomix.island.services.AtomixNotificationService
import com.atomix.island.services.SystemEventReceiver
import com.atomix.island.ui.components.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IslandViewModel @Inject constructor() : ViewModel() {

    private val _islandState = MutableStateFlow<IslandState>(IslandState.Compact)
    val islandState: StateFlow<IslandState> = _islandState.asStateFlow()

    private val _currentEvent = MutableStateFlow<IslandEvent>(IslandEvent.Idle)
    val currentEvent: StateFlow<IslandEvent> = _currentEvent.asStateFlow()

    init {
        observeNotifications()
        observeSystemEvents()
        observeMedia()
    }

    private fun observeNotifications() {
        viewModelScope.launch {
            AtomixNotificationService.notificationFlow.collect { notification ->
                showEvent(notification, durationMs = 5000)
            }
        }
    }

    private fun observeSystemEvents() {
        viewModelScope.launch {
            SystemEventReceiver.eventFlow.collect { event ->
                val duration = when (event) {
                    is IslandEvent.VolumeChange   -> 2000L
                    is IslandEvent.BatteryCharging-> 3000L
                    is IslandEvent.BatteryLow     -> 5000L
                    else                          -> 3000L
                }
                showEvent(event, duration)
            }
        }
    }

    private fun observeMedia() {
        viewModelScope.launch {
            AtomixMediaService.mediaState.collect { music ->
                music?.let { showEvent(it, durationMs = -1) } // -1 = persistent
            }
        }
    }

    fun showEvent(event: IslandEvent, durationMs: Long = 4000) {
        viewModelScope.launch {
            _currentEvent.value = event
            val newState = when (event) {
                is IslandEvent.Music          -> IslandState.Expanded(event)
                is IslandEvent.IncomingCall   -> IslandState.FullIsland(event)
                is IslandEvent.ActiveCall     -> IslandState.Expanded(event)
                is IslandEvent.VoiceAssistant -> IslandState.Expanded(event)
                is IslandEvent.Notification   -> IslandState.Expanded(event)
                is IslandEvent.VolumeChange   -> IslandState.Mini(event)
                is IslandEvent.BatteryCharging-> IslandState.Expanded(event)
                is IslandEvent.BatteryLow     -> IslandState.Expanded(event)
                is IslandEvent.Navigation     -> IslandState.Expanded(event)
                is IslandEvent.Download       -> IslandState.Expanded(event)
                is IslandEvent.Idle           -> IslandState.Compact
                else                          -> IslandState.Mini(event)
            }
            _islandState.value = newState

            if (durationMs > 0) {
                delay(durationMs)
                if (_islandState.value == newState) { // hasn't changed
                    collapse()
                }
            }
        }
    }

    fun expand(event: IslandEvent) {
        _islandState.value = IslandState.FullIsland(event)
    }

    fun collapse() {
        _islandState.value = IslandState.Compact
        _currentEvent.value = IslandEvent.Idle
    }

    fun showSplitView(left: IslandEvent, right: IslandEvent) {
        _islandState.value = IslandState.SplitIsland(left, right)
    }
}
