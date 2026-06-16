package com.atomix.island.ui.widgets

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atomix.island.animations.glowPulseAnimation
import com.atomix.island.animations.wavePhaseAnimation
import com.atomix.island.ui.components.AssistantState
import com.atomix.island.ui.components.IslandEvent
import com.atomix.island.ui.theme.AtomixColors
import kotlin.math.*

// ─── Voice Assistant Expanded ─────────────────────────────────────────────────
@Composable
fun VoiceAssistantIsland(event: IslandEvent.VoiceAssistant) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Animated orb
        AssistantOrb(state = event.state, size = 40.dp)

        // State label
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when (event.state) {
                    AssistantState.IDLE       -> "Assistant"
                    AssistantState.LISTENING  -> "Listening…"
                    AssistantState.THINKING   -> "Thinking…"
                    AssistantState.RESPONDING -> "Speaking…"
                },
                color      = AtomixColors.TextPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (event.responseText.isNotEmpty()) {
                Text(
                    text     = event.responseText,
                    color    = AtomixColors.TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        // Waveform
        MiniWaveform(
            state    = event.state,
            modifier = Modifier.size(60.dp, 32.dp)
        )
    }
}

// ─── Voice Assistant Full ─────────────────────────────────────────────────────
@Composable
fun VoiceAssistantIslandFull(event: IslandEvent.VoiceAssistant) {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement   = Arrangement.SpaceBetween,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        // Large orb
        AssistantOrb(state = event.state, size = 72.dp)

        // State text
        Text(
            text = when (event.state) {
                AssistantState.IDLE       -> "How can I help?"
                AssistantState.LISTENING  -> "I'm listening…"
                AssistantState.THINKING   -> "Let me think…"
                AssistantState.RESPONDING -> event.responseText.ifEmpty { "Here you go…" }
            },
            color      = AtomixColors.TextPrimary,
            fontSize   = 14.sp,
            fontWeight = FontWeight.Medium
        )

        // Full waveform
        AnimatedWaveform(
            state    = event.state,
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        )
    }
}

// ─── Assistant Orb ────────────────────────────────────────────────────────────
@Composable
fun AssistantOrb(state: AssistantState, size: androidx.compose.ui.unit.Dp) {
    val glowAlpha = glowPulseAnimation(0.3f, 0.8f, when (state) {
        AssistantState.LISTENING  -> 800
        AssistantState.THINKING   -> 1200
        AssistantState.RESPONDING -> 600
        else                       -> 2000
    })

    val infiniteTransition = rememberInfiniteTransition(label = "orbRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 360f,
        animationSpec = infiniteRepeatable(
            tween(if (state == AssistantState.IDLE) 8000 else 3000, easing = LinearEasing)
        ),
        label = "orbRot"
    )

    Box(
        modifier = Modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow
        Box(
            modifier = Modifier
                .size(size + 12.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            AtomixColors.PurpleGlow.copy(alpha = glowAlpha * 0.5f),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )

        // Orb
        Canvas(modifier = Modifier.size(size)) {
            val center = Offset(this.size.width / 2, this.size.height / 2)
            val radius = this.size.minDimension / 2

            // Base gradient
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        AtomixColors.PurpleGlow,
                        AtomixColors.ElectricBlue,
                        AtomixColors.MintGreen,
                        AtomixColors.PurpleGlow,
                    ),
                    center = center
                ),
                radius = radius,
                center = center,
            )

            // Inner dark core
            drawCircle(
                color  = AtomixColors.PureBlack.copy(alpha = 0.5f),
                radius = radius * 0.6f,
                center = center
            )

            // Highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(center.x - radius * 0.25f, center.y - radius * 0.25f),
                    radius = radius * 0.5f
                ),
                radius = radius,
                center = center
            )
        }

        // State indicator
        when (state) {
            AssistantState.LISTENING -> {
                // Ripple rings
                RippleRings(color = AtomixColors.PurpleGlow, size = size)
            }
            AssistantState.THINKING -> {
                // Rotating dots
                ThinkingDots(size = size * 0.4f)
            }
            else -> {}
        }
    }
}

// ─── Ripple Rings ─────────────────────────────────────────────────────────────
@Composable
fun RippleRings(color: Color, size: androidx.compose.ui.unit.Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "ripple")
    val ripple1 by infiniteTransition.animateFloat(0f, 1f,
        infiniteRepeatable(tween(1200), RepeatMode.Restart), "r1")
    val ripple2 by infiniteTransition.animateFloat(0f, 1f,
        infiniteRepeatable(tween(1200, delayMillis = 400), RepeatMode.Restart), "r2")

    Canvas(modifier = Modifier.size(size)) {
        fun drawRipple(progress: Float) {
            drawCircle(
                color  = color.copy(alpha = (1f - progress) * 0.4f),
                radius = (this.size.minDimension / 2) * (0.6f + progress * 0.5f),
                style  = androidx.compose.ui.graphics.drawscope.Stroke(2f)
            )
        }
        drawRipple(ripple1)
        drawRipple(ripple2)
    }
}

// ─── Thinking Dots ────────────────────────────────────────────────────────────
@Composable
fun ThinkingDots(size: androidx.compose.ui.unit.Dp) {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val phase by infiniteTransition.animateFloat(
        0f, (2 * PI).toFloat(),
        infiniteRepeatable(tween(900, easing = LinearEasing)),
        "thinkPhase"
    )
    Canvas(modifier = Modifier.size(size)) {
        val cx = this.size.width / 2
        val cy = this.size.height / 2
        val r = this.size.minDimension * 0.35f
        repeat(3) { i ->
            val angle = phase + i * (2 * PI / 3).toFloat()
            val x = cx + r * cos(angle)
            val y = cy + r * sin(angle)
            val dotAlpha = 0.4f + 0.6f * ((sin(phase * 3 + i * PI.toFloat()) + 1f) / 2f)
            drawCircle(
                color  = Color.White.copy(alpha = dotAlpha),
                radius = this.size.minDimension * 0.08f,
                center = Offset(x, y)
            )
        }
    }
}

// ─── Mini Waveform ────────────────────────────────────────────────────────────
@Composable
fun MiniWaveform(state: AssistantState, modifier: Modifier) {
    val phase = if (state != AssistantState.IDLE) wavePhaseAnimation(500) else 0f
    val amplitude = when (state) {
        AssistantState.LISTENING  -> 0.8f
        AssistantState.RESPONDING -> 1f
        AssistantState.THINKING   -> 0.3f
        else                       -> 0f
    }

    Canvas(modifier = modifier) {
        drawWaveform(
            phase     = phase,
            amplitude = amplitude,
            color     = AtomixColors.PurpleGlow,
            bars      = 12,
        )
    }
}

// ─── Full Waveform ────────────────────────────────────────────────────────────
@Composable
fun AnimatedWaveform(state: AssistantState, modifier: Modifier) {
    val phase = if (state != AssistantState.IDLE) wavePhaseAnimation(600) else 0f
    val amplitude = when (state) {
        AssistantState.LISTENING  -> 0.75f
        AssistantState.RESPONDING -> 1f
        AssistantState.THINKING   -> 0.25f
        else                       -> 0f
    }

    Canvas(modifier = modifier) {
        drawWaveformLine(phase, amplitude,
            Brush.horizontalGradient(listOf(AtomixColors.PurpleGlow, AtomixColors.ElectricBlue, AtomixColors.MintGreen))
        )
    }
}

private fun DrawScope.drawWaveform(
    phase: Float,
    amplitude: Float,
    color: Color,
    bars: Int,
) {
    val barWidth   = this.size.width / (bars * 2f)
    val maxBarHeight = this.size.height * 0.9f

    repeat(bars) { i ->
        val x = i * (this.size.width / bars) + barWidth / 2
        val barH = maxBarHeight * amplitude * (0.3f + 0.7f * ((sin(phase + i * 0.8f) + 1f) / 2f))
        val top  = (this.size.height - barH) / 2f

        drawRoundRect(
            color       = color.copy(alpha = 0.7f + 0.3f * amplitude),
            topLeft     = Offset(x, top),
            size        = androidx.compose.ui.geometry.Size(barWidth, barH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2)
        )
    }
}

private fun DrawScope.drawWaveformLine(phase: Float, amplitude: Float, brush: Brush) {
    val path = Path()
    val steps = 100
    path.moveTo(0f, this.size.height / 2)
    repeat(steps) { i ->
        val x = i * this.size.width / steps.toFloat()
        val y = this.size.height / 2 + amplitude *
                (this.size.height * 0.4f) * sin(phase + i * 0.2f)
        path.lineTo(x, y)
    }
    drawPath(
        path  = path,
        brush = brush,
        style = androidx.compose.ui.graphics.drawscope.Stroke(
            width      = 2.5f,
            cap        = StrokeCap.Round,
            join       = StrokeJoin.Round
        )
    )
}
