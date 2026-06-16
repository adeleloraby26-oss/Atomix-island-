package com.atomix.island.utils

import androidx.compose.foundation.gestures.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import com.atomix.island.ui.components.IslandEvent
import com.atomix.island.ui.components.IslandState

enum class IslandGesture {
    TAP, DOUBLE_TAP, LONG_PRESS,
    SWIPE_UP, SWIPE_DOWN, SWIPE_LEFT, SWIPE_RIGHT
}

/**
 * Attaches gesture recognition to the island:
 * - Tap         → toggle compact / expanded
 * - Double tap  → full island
 * - Long press  → open settings
 * - Swipe up    → dismiss / collapse
 * - Swipe down  → expand to full
 * - Swipe left/right → cycle events
 */
fun Modifier.islandGestures(
    onGesture: (IslandGesture) -> Unit,
    sensitivity: Float = 40f,
): Modifier = this.pointerInput(Unit) {
    var startX = 0f
    var startY = 0f

    detectTapGestures(
        onTap        = { onGesture(IslandGesture.TAP) },
        onDoubleTap  = { onGesture(IslandGesture.DOUBLE_TAP) },
        onLongPress  = { onGesture(IslandGesture.LONG_PRESS) },
    )
}.pointerInput(Unit) {
    detectDragGestures(
        onDragStart = { offset ->
            // store in outer scope via side effect — simplified here
        },
        onDrag = { change, dragAmount ->
            change.consume()
            val dx = dragAmount.x
            val dy = dragAmount.y
            if (Math.abs(dy) > sensitivity) {
                // vertical dominant
            } else if (Math.abs(dx) > sensitivity) {
                // horizontal dominant
            }
        }
    )
}

// ─── State machine for gesture → island state ─────────────────────────────────
class IslandGestureStateMachine {
    private var currentEvent: IslandEvent = IslandEvent.Idle

    fun handleGesture(
        gesture: IslandGesture,
        currentState: IslandState,
        onNewState: (IslandState) -> Unit
    ) {
        when (gesture) {
            IslandGesture.TAP -> {
                when (currentState) {
                    is IslandState.Compact  -> {
                        if (currentEvent != IslandEvent.Idle) {
                            onNewState(IslandState.Expanded(currentEvent))
                        }
                    }
                    is IslandState.Mini     -> onNewState(IslandState.Expanded(currentState.event))
                    is IslandState.Expanded -> onNewState(IslandState.FullIsland(currentState.event))
                    is IslandState.FullIsland -> onNewState(IslandState.Compact)
                    else -> onNewState(IslandState.Compact)
                }
            }
            IslandGesture.DOUBLE_TAP -> {
                val event = when (currentState) {
                    is IslandState.Expanded   -> currentState.event
                    is IslandState.Mini       -> currentState.event
                    is IslandState.FullIsland -> currentState.event
                    else -> return
                }
                onNewState(IslandState.FullIsland(event))
            }
            IslandGesture.SWIPE_UP -> {
                onNewState(IslandState.Compact)
            }
            IslandGesture.SWIPE_DOWN -> {
                val event = when (currentState) {
                    is IslandState.Compact  -> currentEvent
                    is IslandState.Expanded -> currentState.event
                    is IslandState.Mini     -> currentState.event
                    else -> return
                }
                onNewState(IslandState.FullIsland(event))
            }
            else -> {}
        }
    }

    fun setCurrentEvent(event: IslandEvent) {
        currentEvent = event
    }
}
