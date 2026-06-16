package com.atomix.island.animations

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.atomix.island.ui.theme.AtomixColors
import kotlin.math.*
import kotlin.random.Random

data class Particle(
    val id: Int,
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var alpha: Float,
    var scale: Float,
    var color: Color,
    var life: Float = 1f,
)

@Composable
fun ParticleEffect(
    modifier: Modifier = Modifier,
    particleCount: Int = 20,
    color: Color = AtomixColors.ElectricBlue,
    active: Boolean = true,
) {
    if (!active) return

    var particles by remember {
        mutableStateOf(
            List(particleCount) { i ->
                Particle(
                    id     = i,
                    x      = Random.nextFloat(),
                    y      = Random.nextFloat(),
                    vx     = (Random.nextFloat() - 0.5f) * 0.003f,
                    vy     = -(Random.nextFloat() * 0.004f + 0.001f),
                    alpha  = Random.nextFloat() * 0.6f + 0.1f,
                    scale  = Random.nextFloat() * 0.8f + 0.2f,
                    color  = color.copy(alpha = Random.nextFloat() * 0.6f + 0.2f),
                    life   = Random.nextFloat(),
                )
            }
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val tick by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(16, easing = LinearEasing)),
        label         = "particleTick"
    )

    LaunchedEffect(tick) {
        particles = particles.map { p ->
            var newX = p.x + p.vx
            var newY = p.y + p.vy
            var newLife = p.life - 0.008f

            // Respawn
            if (newLife <= 0f || newY < -0.1f || newX < -0.1f || newX > 1.1f) {
                newX    = Random.nextFloat()
                newY    = 1.1f
                newLife = 1f
            }

            val newAlpha = (newLife * 0.7f).coerceIn(0f, 0.7f)

            p.copy(x = newX, y = newY, life = newLife, alpha = newAlpha)
        }
    }

    Canvas(modifier = modifier) {
        particles.forEach { p ->
            val px    = p.x * size.width
            val py    = p.y * size.height
            val r     = size.minDimension * 0.012f * p.scale

            drawCircle(
                color  = p.color.copy(alpha = p.alpha),
                radius = r,
                center = Offset(px, py)
            )
        }
    }
}

// ─── Sparkle Effect ───────────────────────────────────────────────────────────
@Composable
fun SparkleEffect(
    modifier: Modifier = Modifier,
    color: Color = AtomixColors.GoldenAmber,
    count: Int = 8,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val phase by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label         = "sparklePhase"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f

        repeat(count) { i ->
            val angle  = phase + i * (2 * PI / count).toFloat()
            val radius = size.minDimension * 0.35f
            val x      = cx + radius * cos(angle)
            val y      = cy + radius * sin(angle)
            val alpha  = (sin(phase * 2 + i * 0.8f) + 1f) / 2f * 0.7f

            drawCircle(
                color  = color.copy(alpha = alpha.toFloat()),
                radius = size.minDimension * 0.025f,
                center = Offset(x.toFloat(), y.toFloat())
            )
        }
    }
}

// ─── Glow Ring ────────────────────────────────────────────────────────────────
@Composable
fun GlowRingEffect(
    modifier: Modifier = Modifier,
    color: Color = AtomixColors.PurpleGlow,
    rings: Int = 3,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "glowRing")
    val scale by infiniteTransition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label         = "glowRingScale"
    )

    Canvas(modifier = modifier) {
        repeat(rings) { i ->
            val ringProgress = ((scale + i.toFloat() / rings) % 1f)
            val radius       = size.minDimension * 0.5f * ringProgress
            val alpha        = (1f - ringProgress) * 0.5f

            drawCircle(
                color  = color.copy(alpha = alpha),
                radius = radius,
                center = Offset(size.width / 2, size.height / 2),
                style  = androidx.compose.ui.graphics.drawscope.Stroke(2f)
            )
        }
    }
}
