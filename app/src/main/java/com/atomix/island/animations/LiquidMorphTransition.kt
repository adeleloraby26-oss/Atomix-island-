package com.atomix.island.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.*

/**
 * Renders a smooth liquid-morphing shape between two rounded-rect states.
 * Used as an alternative background for the island pill during transitions.
 */
@Composable
fun LiquidMorphShape(
    modifier: Modifier = Modifier,
    fromWidth: Float,
    fromHeight: Float,
    fromRadius: Float,
    toWidth: Float,
    toHeight: Float,
    toRadius: Float,
    color: Color,
    progress: Float,  // 0..1
) {
    val w = lerp(fromWidth, toWidth, progress)
    val h = lerp(fromHeight, toHeight, progress)
    val r = lerp(fromRadius, toRadius, progress)

    Canvas(modifier = modifier) {
        val scaleX = size.width / w
        val scaleY = size.height / h
        val cx = size.width  / 2f
        val cy = size.height / 2f

        // Squircle-style super-ellipse for liquid feel
        drawLiquidPill(
            cx     = cx,
            cy     = cy,
            width  = size.width,
            height = size.height,
            radius = (r * ((scaleX + scaleY) / 2f)).coerceAtMost(size.minDimension / 2f),
            color  = color,
            wobble = sin(progress * PI.toFloat()) * 0.04f, // mid-transition wobble
        )
    }
}

private fun DrawScope.drawLiquidPill(
    cx: Float, cy: Float,
    width: Float, height: Float,
    radius: Float,
    color: Color,
    wobble: Float = 0f,
) {
    val path = Path().apply {
        val hw = width  / 2f
        val hh = height / 2f
        val r  = radius.coerceAtMost(minOf(hw, hh))

        // Top-left
        moveTo(cx - hw + r, cy - hh)
        // Top edge with subtle wobble
        quadraticBezierTo(
            cx, cy - hh - height * wobble,
            cx + hw - r, cy - hh
        )
        // Top-right corner
        arcTo(
            rect       = Rect(cx + hw - r * 2, cy - hh, cx + hw, cy - hh + r * 2),
            startAngleDegrees  = -90f,
            sweepAngleDegrees  = 90f,
            forceMoveTo = false
        )
        // Right edge
        lineTo(cx + hw, cy + hh - r)
        // Bottom-right corner
        arcTo(
            rect       = Rect(cx + hw - r * 2, cy + hh - r * 2, cx + hw, cy + hh),
            startAngleDegrees  = 0f,
            sweepAngleDegrees  = 90f,
            forceMoveTo = false
        )
        // Bottom edge with wobble
        quadraticBezierTo(
            cx, cy + hh + height * wobble,
            cx - hw + r, cy + hh
        )
        // Bottom-left corner
        arcTo(
            rect       = Rect(cx - hw, cy + hh - r * 2, cx - hw + r * 2, cy + hh),
            startAngleDegrees  = 90f,
            sweepAngleDegrees  = 90f,
            forceMoveTo = false
        )
        // Left edge
        lineTo(cx - hw, cy - hh + r)
        // Top-left corner
        arcTo(
            rect       = Rect(cx - hw, cy - hh, cx - hw + r * 2, cy - hh + r * 2),
            startAngleDegrees  = 180f,
            sweepAngleDegrees  = 90f,
            forceMoveTo = false
        )
        close()
    }
    drawPath(path, color)
}

private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t

// ─── Island State Transition Animation ────────────────────────────────────────
@Composable
fun rememberIslandMorphProgress(target: Boolean): Float {
    val progress by animateFloatAsState(
        targetValue   = if (target) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.72f,
            stiffness    = 420f
        ),
        label = "morphProgress"
    )
    return progress
}
