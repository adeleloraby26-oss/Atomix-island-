package com.atomix.island.ui.widgets

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.atomix.island.animations.breathingAnimation
import com.atomix.island.animations.wavePhaseAnimation
import com.atomix.island.ui.components.IslandEvent
import com.atomix.island.ui.theme.AtomixColors
import kotlin.math.sin

// ─── Expanded Music Island ────────────────────────────────────────────────────
@Composable
fun MusicIslandExpanded(event: IslandEvent.Music) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Album art
        AlbumArtThumbnail(
            url  = event.albumArt,
            size = 48.dp,
            isPlaying = event.isPlaying
        )

        // Track info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text       = event.title,
                color      = AtomixColors.TextPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = event.artist,
                color    = AtomixColors.TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Progress bar
            MusicProgressBar(progress = event.progress, color = AtomixColors.SunriseOrange)
        }

        // Play/Pause
        MediaControlButton(
            isPlaying = event.isPlaying,
            tint      = AtomixColors.White
        )
    }
}

// ─── Full Music Island ────────────────────────────────────────────────────────
@Composable
fun MusicIslandFull(event: IslandEvent.Music) {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement   = Arrangement.SpaceBetween
    ) {
        // Top row: art + info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AlbumArtThumbnail(
                url       = event.albumArt,
                size      = 56.dp,
                isPlaying = event.isPlaying
            )
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text       = event.title,
                    color      = AtomixColors.TextPrimary,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Text(
                    text     = event.artist,
                    color    = AtomixColors.TextSecondary,
                    fontSize = 12.sp,
                )
            }
        }

        // Progress
        MusicProgressBar(progress = event.progress, color = AtomixColors.SunriseOrange)

        // Controls
        MusicControls(event)
    }
}

// ─── Album Art Thumbnail ──────────────────────────────────────────────────────
@Composable
fun AlbumArtThumbnail(
    url: String?,
    size: androidx.compose.ui.unit.Dp,
    isPlaying: Boolean = true,
) {
    val breathScale = if (isPlaying) breathingAnimation(0.95f, 1.05f, 1800) else 1f

    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(size / 5))
            .background(AtomixColors.Graphite),
        contentAlignment = Alignment.Center
    ) {
        if (url != null) {
            AsyncImage(
                model         = url,
                contentDescription = "Album Art",
                contentScale  = ContentScale.Crop,
                modifier      = Modifier.fillMaxSize()
            )
        } else {
            // Placeholder with equalizer animation
            EqualizerBars(isPlaying = isPlaying, color = AtomixColors.SunriseOrange)
        }

        // Playing indicator overlay
        if (isPlaying) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.15f))
            )
        }
    }
}

// ─── Equalizer Bars ───────────────────────────────────────────────────────────
@Composable
fun EqualizerBars(isPlaying: Boolean, color: Color, bars: Int = 4) {
    val phase = if (isPlaying) wavePhaseAnimation(600) else 0f

    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment     = Alignment.Bottom,
        modifier              = Modifier.height(24.dp)
    ) {
        repeat(bars) { i ->
            val barHeight = if (isPlaying) {
                (0.3f + 0.7f * ((sin(phase + i * 1.2f) + 1f) / 2f)) * 24f
            } else {
                6f
            }
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(barHeight.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(color, color.copy(alpha = 0.4f))
                        )
                    )
            )
        }
    }
}

// ─── Progress Bar ─────────────────────────────────────────────────────────────
@Composable
fun MusicProgressBar(progress: Float, color: Color = AtomixColors.ElectricBlue) {
    val animatedProgress by animateFloatAsState(
        targetValue   = progress,
        animationSpec = tween(500, easing = LinearEasing),
        label         = "musicProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(AtomixColors.SurfaceGlass)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            AtomixColors.GradientMusicEnd,
                            color
                        )
                    )
                )
        )
        // Thumb dot
        Box(
            modifier = Modifier
                .offset(x = (animatedProgress * 1f).dp)
                .align(Alignment.CenterStart)
                .padding(start = (animatedProgress * 300 - 4).coerceAtLeast(0f).dp)
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

// ─── Play / Pause Button ──────────────────────────────────────────────────────
@Composable
fun MediaControlButton(
    isPlaying: Boolean,
    tint: Color = AtomixColors.White,
    onClick: () -> Unit = {}
) {
    val scale by animateFloatAsState(
        targetValue   = 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 800f),
        label         = "mediaCtrl"
    )
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(AtomixColors.SurfaceGlass)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Play / Pause icon drawn as SVG path
        androidx.compose.foundation.Canvas(Modifier.size(16.dp)) {
            if (isPlaying) {
                // Pause: two bars
                drawRect(tint, topLeft = androidx.compose.ui.geometry.Offset(3f, 2f),
                    size = androidx.compose.ui.geometry.Size(4f, 12f))
                drawRect(tint, topLeft = androidx.compose.ui.geometry.Offset(9f, 2f),
                    size = androidx.compose.ui.geometry.Size(4f, 12f))
            } else {
                // Play triangle
                val path = Path().apply {
                    moveTo(3f, 1f); lineTo(15f, 8f); lineTo(3f, 15f); close()
                }
                drawPath(path, tint)
            }
        }
    }
}

// ─── Full Music Controls ──────────────────────────────────────────────────────
@Composable
fun MusicControls(event: IslandEvent.Music) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        // Previous
        ControlIcon(
            onClick  = {},
            size     = 28.dp,
            content  = {
                androidx.compose.foundation.Canvas(Modifier.size(14.dp)) {
                    val path = Path().apply {
                        moveTo(12f, 1f); lineTo(4f, 7f); lineTo(12f, 13f); close()
                        moveTo(4f, 1f); lineTo(2f, 1f); lineTo(2f, 13f); lineTo(4f, 13f); close()
                    }
                    drawPath(path, Color.White)
                }
            }
        )

        // Play / Pause (larger)
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(AtomixColors.GradientMusicStart, AtomixColors.GradientMusicEnd)
                    )
                )
                .clickable {},
            contentAlignment = Alignment.Center
        ) {
            MediaControlButton(isPlaying = event.isPlaying, tint = Color.White)
        }

        // Next
        ControlIcon(
            onClick = {},
            size    = 28.dp,
            content = {
                androidx.compose.foundation.Canvas(Modifier.size(14.dp)) {
                    val path = Path().apply {
                        moveTo(2f, 1f); lineTo(10f, 7f); lineTo(2f, 13f); close()
                        moveTo(10f, 1f); lineTo(12f, 1f); lineTo(12f, 13f); lineTo(10f, 13f); close()
                    }
                    drawPath(path, Color.White)
                }
            }
        )
    }
}

@Composable
fun ControlIcon(
    onClick: () -> Unit,
    size: androidx.compose.ui.unit.Dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(AtomixColors.SurfaceGlassDim)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
        content = { content() }
    )
}
