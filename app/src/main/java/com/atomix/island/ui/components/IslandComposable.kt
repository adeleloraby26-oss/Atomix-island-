package com.atomix.island.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atomix.island.animations.IslandSizeSpec
import com.atomix.island.animations.breathingAnimation
import com.atomix.island.animations.glowPulseAnimation
import com.atomix.island.ui.theme.AtomixColors
import com.atomix.island.ui.theme.atomix
import com.atomix.island.ui.widgets.*

// ─── Main Atomix Island ───────────────────────────────────────────────────────
@Composable
fun AtomixIsland(
    state: IslandState,
    onTap: () -> Unit = {},
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val haptic = LocalHapticFeedback.current

    // Resolve target dimensions from state
    val (targetWidth, targetHeight, targetRadius) = resolveIslandDimensions(state)
    val (width, height, radius) = animatedIslandShape(targetWidth, targetHeight, targetRadius)

    // Glow color based on current event
    val glowColor = resolveGlowColor(state)
    val glowAlpha = glowPulseAnimation(minAlpha = 0.2f, maxAlpha = 0.45f, durationMs = 2000)

    // Press scale
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue   = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 700f),
        label         = "pressScale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // ── Outer glow layer ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(width + 32.dp, height + 32.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = glowAlpha * 0.6f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(radius + 16.dp)
                )
        )

        // ── Island pill ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .scale(pressScale)
                .size(width, height)
                .glassIsland(
                    cornerRadius    = radius,
                    glowColor       = glowColor.copy(alpha = glowAlpha),
                    backgroundAlpha = 0.96f,
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication        = null
                ) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onTap()
                },
            contentAlignment = Alignment.Center
        ) {
            // ── Inner content switches by state ────────────────────────────
            AnimatedContent(
                targetState   = state,
                transitionSpec = {
                    (fadeIn(tween(200)) + scaleIn(tween(250), initialScale = 0.88f))
                        .togetherWith(fadeOut(tween(150)) + scaleOut(tween(150), targetScale = 0.92f))
                },
                label = "islandContent"
            ) { currentState ->
                when (currentState) {
                    is IslandState.Compact          -> CompactContent()
                    is IslandState.Mini             -> MiniContent(currentState.event)
                    is IslandState.Expanded         -> ExpandedContent(currentState.event)
                    is IslandState.FullIsland       -> FullIslandContent(currentState.event)
                    is IslandState.SplitIsland      -> SplitContent(currentState.left, currentState.right)
                    is IslandState.DualIsland       -> DualContent(currentState.primary, currentState.secondary)
                }
            }
        }
    }
}

// ─── Compact ──────────────────────────────────────────────────────────────────
@Composable
private fun CompactContent() {
    // Just a camera dot indicator
    Row(
        modifier            = Modifier.padding(horizontal = 12.dp),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Camera dot
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(AtomixColors.RoseRed.copy(alpha = 0.9f))
        )
    }
}

// ─── Mini ─────────────────────────────────────────────────────────────────────
@Composable
private fun MiniContent(event: IslandEvent) {
    Row(
        modifier              = Modifier.padding(horizontal = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        EventIconMini(event)
        EventLabelMini(event)
    }
}

// ─── Expanded ─────────────────────────────────────────────────────────────────
@Composable
private fun ExpandedContent(event: IslandEvent) {
    when (event) {
        is IslandEvent.Music          -> MusicIslandExpanded(event)
        is IslandEvent.IncomingCall   -> IncomingCallIsland(event)
        is IslandEvent.ActiveCall     -> ActiveCallIsland(event)
        is IslandEvent.VoiceAssistant -> VoiceAssistantIsland(event)
        is IslandEvent.Navigation     -> NavigationIsland(event)
        is IslandEvent.BatteryCharging-> BatteryChargingIsland(event)
        is IslandEvent.Notification   -> NotificationIslandExpanded(event)
        is IslandEvent.VolumeChange   -> VolumeIsland(event)
        is IslandEvent.Download       -> DownloadIsland(event)
        else                          -> GenericExpandedContent(event)
    }
}

// ─── Full Island ──────────────────────────────────────────────────────────────
@Composable
private fun FullIslandContent(event: IslandEvent) {
    when (event) {
        is IslandEvent.Music          -> MusicIslandFull(event)
        is IslandEvent.VoiceAssistant -> VoiceAssistantIslandFull(event)
        is IslandEvent.IncomingCall   -> IncomingCallIslandFull(event)
        else                          -> ExpandedContent(event)
    }
}

// ─── Split ────────────────────────────────────────────────────────────────────
@Composable
private fun SplitContent(left: IslandEvent, right: IslandEvent) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(Modifier.weight(1f)) { EventIconMini(left) }
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.6f)
                .background(AtomixColors.SurfaceBorder)
        )
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            EventIconMini(right)
        }
    }
}

// ─── Dual ─────────────────────────────────────────────────────────────────────
@Composable
private fun DualContent(primary: IslandEvent, secondary: IslandEvent) {
    SplitContent(primary, secondary)
}

// ─── Helpers ──────────────────────────────────────────────────────────────────
@Composable
private fun GenericExpandedContent(event: IslandEvent) {
    Row(
        modifier              = Modifier.padding(horizontal = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EventIconMini(event)
        Text(
            text       = eventLabel(event),
            color      = AtomixColors.White,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun resolveIslandDimensions(state: IslandState): Triple<Dp, Dp, Dp> = when (state) {
    is IslandState.Compact    -> Triple(IslandSizeSpec.COMPACT_WIDTH,  IslandSizeSpec.COMPACT_HEIGHT,  IslandSizeSpec.COMPACT_RADIUS)
    is IslandState.Mini       -> Triple(IslandSizeSpec.MINI_WIDTH,     IslandSizeSpec.MINI_HEIGHT,     IslandSizeSpec.COMPACT_RADIUS)
    is IslandState.Expanded   -> Triple(IslandSizeSpec.EXPANDED_WIDTH, IslandSizeSpec.EXPANDED_HEIGHT, IslandSizeSpec.EXPANDED_RADIUS)
    is IslandState.FullIsland -> Triple(IslandSizeSpec.FULL_WIDTH,     IslandSizeSpec.FULL_HEIGHT,     IslandSizeSpec.FULL_RADIUS)
    is IslandState.SplitIsland-> Triple(IslandSizeSpec.EXPANDED_WIDTH, IslandSizeSpec.COMPACT_HEIGHT,  IslandSizeSpec.COMPACT_RADIUS)
    is IslandState.DualIsland -> Triple(IslandSizeSpec.EXPANDED_WIDTH, IslandSizeSpec.COMPACT_HEIGHT,  IslandSizeSpec.COMPACT_RADIUS)
}

private fun resolveGlowColor(state: IslandState): Color = when (state) {
    is IslandState.Compact           -> Color.Transparent
    is IslandState.Mini              -> resolveEventGlow(state.event)
    is IslandState.Expanded          -> resolveEventGlow(state.event)
    is IslandState.FullIsland        -> resolveEventGlow(state.event)
    is IslandState.SplitIsland       -> resolveEventGlow(state.left)
    is IslandState.DualIsland        -> resolveEventGlow(state.primary)
}

private fun resolveEventGlow(event: IslandEvent): Color = when (event) {
    is IslandEvent.Music          -> AtomixColors.SunriseOrange
    is IslandEvent.IncomingCall   -> AtomixColors.MintGreen
    is IslandEvent.ActiveCall     -> AtomixColors.MintGreen
    is IslandEvent.VoiceAssistant -> AtomixColors.PurpleGlow
    is IslandEvent.BatteryCharging-> AtomixColors.GoldenAmber
    is IslandEvent.BatteryLow     -> AtomixColors.RoseRed
    is IslandEvent.Navigation     -> AtomixColors.ElectricBlue
    is IslandEvent.Download       -> AtomixColors.ElectricBlue
    else                          -> AtomixColors.ElectricBlue
}

private fun eventLabel(event: IslandEvent): String = when (event) {
    is IslandEvent.Screenshot         -> "Screenshot captured"
    is IslandEvent.ScreenRecordingStart -> "Recording started"
    is IslandEvent.ScreenRecordingStop  -> "Recording stopped"
    is IslandEvent.CameraActive        -> "Camera in use"
    is IslandEvent.MicrophoneActive    -> "Microphone in use"
    is IslandEvent.AlarmFiring         -> "Alarm: ${event.label}"
    is IslandEvent.TimerFinished       -> "Timer done"
    is IslandEvent.HeadphonesConnected -> "${event.name} connected"
    else                               -> ""
}

@Composable
private fun EventIconMini(event: IslandEvent) {
    // Minimal icon dot per event type
    val (color, size) = when (event) {
        is IslandEvent.Music          -> Pair(AtomixColors.SunriseOrange, 20.dp)
        is IslandEvent.IncomingCall   -> Pair(AtomixColors.MintGreen, 20.dp)
        is IslandEvent.ActiveCall     -> Pair(AtomixColors.MintGreen, 8.dp)
        is IslandEvent.VoiceAssistant -> Pair(AtomixColors.PurpleGlow, 20.dp)
        is IslandEvent.BatteryCharging-> Pair(AtomixColors.GoldenAmber, 20.dp)
        is IslandEvent.Navigation     -> Pair(AtomixColors.ElectricBlue, 20.dp)
        else                          -> Pair(AtomixColors.ElectricBlue, 8.dp)
    }
    Box(
        Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
private fun EventLabelMini(event: IslandEvent) {
    val label = when (event) {
        is IslandEvent.Music          -> event.title
        is IslandEvent.ActiveCall     -> "Active Call"
        is IslandEvent.VolumeChange   -> "Volume ${event.level}"
        is IslandEvent.BatteryCharging-> "${event.level}%"
        is IslandEvent.Navigation     -> event.instruction
        else                          -> eventLabel(event)
    }
    if (label.isNotEmpty()) {
        Text(
            text       = label,
            color      = AtomixColors.White,
            fontSize   = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines   = 1
        )
    }
}
