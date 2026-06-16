package com.atomix.island.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.atomix.island.animations.AtomixSprings
import com.atomix.island.ui.theme.AtomixColors

// ─── Animated Island Shape ────────────────────────────────────────────────────
@Composable
fun animatedIslandShape(
    targetWidth: Dp,
    targetHeight: Dp,
    targetRadius: Dp,
): Triple<Dp, Dp, Dp> {
    val width by animateDpAsState(
        targetValue   = targetWidth,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 500f),
        label         = "islandWidth"
    )
    val height by animateDpAsState(
        targetValue   = targetHeight,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 480f),
        label         = "islandHeight"
    )
    val radius by animateDpAsState(
        targetValue   = targetRadius,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = 400f),
        label         = "islandRadius"
    )
    return Triple(width, height, radius)
}

// ─── Glass Island Modifier ─────────────────────────────────────────────────────
fun Modifier.glassIsland(
    cornerRadius: Dp = 26.dp,
    glowColor: Color = AtomixColors.ElectricBlueGlow,
    glowRadius: Dp = 16.dp,
    borderAlpha: Float = 0.18f,
    backgroundAlpha: Float = 0.92f,
): Modifier = this
    .drawBehind {
        // Outer ambient glow
        drawRoundRect(
            brush         = Brush.radialGradient(
                colors  = listOf(glowColor, Color.Transparent),
                radius  = size.maxDimension * 0.8f,
            ),
            cornerRadius  = androidx.compose.ui.geometry.CornerRadius(
                cornerRadius.toPx() + glowRadius.toPx()
            ),
            size          = this.size.copy(
                width  = size.width  + glowRadius.toPx() * 2,
                height = size.height + glowRadius.toPx() * 2,
            ),
            topLeft       = androidx.compose.ui.geometry.Offset(
                -glowRadius.toPx(), -glowRadius.toPx()
            )
        )
    }
    .clip(RoundedCornerShape(cornerRadius))
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                AtomixColors.GraphiteLight.copy(alpha = backgroundAlpha),
                AtomixColors.PureBlack.copy(alpha = backgroundAlpha)
            )
        )
    )
    .drawBehind {
        // Inner border highlight
        drawRoundRect(
            color        = Color.White.copy(alpha = borderAlpha),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius.toPx()),
            style        = Stroke(width = 1.dp.toPx())
        )
    }

// ─── Frosted Glass Modifier ───────────────────────────────────────────────────
fun Modifier.frostedGlass(
    cornerRadius: Dp = 20.dp,
    blurRadius: Dp = 20.dp,
    tint: Color = AtomixColors.SurfaceGlass,
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(tint)

// ─── Island Glow Modifier ─────────────────────────────────────────────────────
fun Modifier.islandGlow(
    color: Color = AtomixColors.ElectricBlue,
    radius: Dp = 24.dp,
    alpha: Float = 0.35f,
): Modifier = this.drawBehind {
    val radiusPx = radius.toPx()
    drawCircle(
        brush  = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            radius = radiusPx * 2
        ),
        radius = radiusPx * 2,
        center = center,
    )
}

// ─── Island Container ─────────────────────────────────────────────────────────
@Composable
fun IslandContainer(
    width: Dp,
    height: Dp,
    cornerRadius: Dp,
    glowColor: Color = AtomixColors.ElectricBlueGlow,
    glowAlpha: Float = 0.3f,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .size(width, height)
            .glassIsland(
                cornerRadius      = cornerRadius,
                glowColor         = glowColor.copy(alpha = glowAlpha),
                backgroundAlpha   = 0.95f,
            ),
        content = content
    )
}
