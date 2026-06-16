package com.atomix.island.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─── Spring Specs ─────────────────────────────────────────────────────────────
object AtomixSprings {

    /** Ultra-snappy for immediate feedback (button presses) */
    val Instant = spring<Float>(
        dampingRatio = 0.7f,
        stiffness    = Spring.StiffnessHigh
    )

    /** Fluid for island size morphing */
    val Fluid = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness    = 500f
    )

    /** Gentle for content fade-ins */
    val Gentle = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness    = 300f
    )

    /** Bouncy for icon entries */
    val Bouncy = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness    = 600f
    )

    /** Liquid for corner-radius changes */
    val Liquid = spring<Dp>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness    = 400f
    )

    /** Scale spring for expand/collapse */
    val Scale = spring<Float>(
        dampingRatio = 0.65f,
        stiffness    = 450f
    )
}

// ─── Tween Specs ─────────────────────────────────────────────────────────────
object AtomixTweens {
    val Fast   = tween<Float>(durationMillis = 150, easing = FastOutSlowInEasing)
    val Medium = tween<Float>(durationMillis = 300, easing = FastOutSlowInEasing)
    val Slow   = tween<Float>(durationMillis = 500, easing = FastOutSlowInEasing)
    val Elastic = tween<Float>(durationMillis = 400, easing = OvershootInterpolator(1.5f).toEasing())
}

// ─── Island Size Transitions ─────────────────────────────────────────────────
object IslandSizeSpec {
    // Compact pill
    val COMPACT_WIDTH  = 126.dp
    val COMPACT_HEIGHT = 34.dp
    val COMPACT_RADIUS = 17.dp

    // Mini
    val MINI_WIDTH  = 200.dp
    val MINI_HEIGHT = 34.dp

    // Expanded
    val EXPANDED_WIDTH  = 340.dp
    val EXPANDED_HEIGHT = 82.dp
    val EXPANDED_RADIUS = 26.dp

    // Full Island
    val FULL_WIDTH  = 360.dp
    val FULL_HEIGHT = 160.dp
    val FULL_RADIUS = 44.dp

    // Split
    val SPLIT_LEFT_WIDTH  = 164.dp
    val SPLIT_RIGHT_WIDTH = 164.dp
    val SPLIT_HEIGHT      = 34.dp
    val SPLIT_GAP         = 8.dp
}

// ─── Easing Extensions ───────────────────────────────────────────────────────
fun android.view.animation.Interpolator.toEasing() = Easing { x -> getInterpolation(x) }

class OvershootInterpolator(private val tension: Float = 2f) :
    android.view.animation.Interpolator {
    override fun getInterpolation(t: Float): Float {
        val t2 = t - 1f
        return t2 * t2 * ((tension + 1f) * t2 + tension) + 1f
    }
}

// ─── Infinite Breathing Animation ────────────────────────────────────────────
@Composable
fun breathingAnimation(
    minScale: Float = 0.97f,
    maxScale: Float = 1.03f,
    durationMs: Int = 2000,
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    val scale by infiniteTransition.animateFloat(
        initialValue = minScale,
        targetValue  = maxScale,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathScale"
    )
    return scale
}

// ─── Pulsing Glow Animation ───────────────────────────────────────────────────
@Composable
fun glowPulseAnimation(
    minAlpha: Float = 0.3f,
    maxAlpha: Float = 0.8f,
    durationMs: Int = 1500,
): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "glowPulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue  = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )
    return alpha
}

// ─── Wave Phase Animation ─────────────────────────────────────────────────────
@Composable
fun wavePhaseAnimation(durationMs: Int = 800): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    return phase
}

// ─── Rotation Animation ───────────────────────────────────────────────────────
@Composable
fun rotationAnimation(durationMs: Int = 3000): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue  = 360f,
        animationSpec = infiniteRepeatable(
            animation  = tween(durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    return rotation
}
