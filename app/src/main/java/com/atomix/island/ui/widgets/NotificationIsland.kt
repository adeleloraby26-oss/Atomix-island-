package com.atomix.island.ui.widgets

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.atomix.island.animations.glowPulseAnimation
import com.atomix.island.ui.components.IslandEvent
import com.atomix.island.ui.components.NotificationPriority
import com.atomix.island.ui.components.StreamType
import com.atomix.island.ui.theme.AtomixColors

// ─── Notification Expanded ────────────────────────────────────────────────────
@Composable
fun NotificationIslandExpanded(event: IslandEvent.Notification) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // App icon / avatar
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(event.accentColor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (event.avatarUrl != null) {
                AsyncImage(model = event.avatarUrl, contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)))
            } else {
                Text(
                    text  = event.appName.firstOrNull()?.uppercase() ?: "?",
                    color = event.accentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    text       = event.appName,
                    color      = event.accentColor,
                    fontSize   = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.3.sp
                )
                if (event.priority == NotificationPriority.URGENT) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(AtomixColors.RoseRed)
                    )
                }
            }
            Text(
                text       = event.title,
                color      = AtomixColors.TextPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text     = event.text,
                color    = AtomixColors.TextSecondary,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ─── Battery Charging ─────────────────────────────────────────────────────────
@Composable
fun BatteryChargingIsland(event: IslandEvent.BatteryCharging) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Battery icon
        BatteryIcon(level = event.level, isCharging = event.isCharging)

        Column {
            Text(
                text       = if (event.isCharging) "Charging" else "Unplugged",
                color      = AtomixColors.TextSecondary,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text       = "${event.level}%",
                color      = batteryColor(event.level),
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.weight(1f))

        // Animated charging bolt
        if (event.isCharging) {
            ChargingBolt()
        }
    }
}

@Composable
private fun BatteryIcon(level: Int, isCharging: Boolean) {
    val color = batteryColor(level)
    Box(
        modifier = Modifier
            .width(32.dp)
            .height(18.dp)
    ) {
        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
            // Battery body
            drawRoundRect(
                color        = color.copy(alpha = 0.3f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f),
                style        = androidx.compose.ui.graphics.drawscope.Stroke(1.5f)
            )
            // Battery tip
            drawRect(
                color   = color.copy(alpha = 0.5f),
                topLeft = androidx.compose.ui.geometry.Offset(size.width - 1f, size.height * 0.3f),
                size    = androidx.compose.ui.geometry.Size(3f, size.height * 0.4f)
            )
            // Fill
            val fillWidth = (size.width - 4f) * (level / 100f)
            drawRoundRect(
                color        = color,
                topLeft      = androidx.compose.ui.geometry.Offset(2f, 2f),
                size         = androidx.compose.ui.geometry.Size(fillWidth, size.height - 4f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f)
            )
        }
    }
}

@Composable
private fun ChargingBolt() {
    val glowAlpha = glowPulseAnimation(0.5f, 1f, 800)
    Text(
        text      = "⚡",
        fontSize  = 18.sp,
        modifier  = Modifier.graphicsLayer(alpha = glowAlpha)
    )
}

private fun batteryColor(level: Int): Color = when {
    level <= 20 -> AtomixColors.RoseRed
    level <= 40 -> AtomixColors.GoldenAmber
    else        -> AtomixColors.MintGreen
}

// ─── Volume Island ────────────────────────────────────────────────────────────
@Composable
fun VolumeIsland(event: IslandEvent.VolumeChange) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Stream icon
        Text(
            text = when (event.streamType) {
                StreamType.MUSIC    -> "🎵"
                StreamType.RING     -> "🔔"
                StreamType.CALL     -> "📞"
                StreamType.ALARM    -> "⏰"
                else                -> "🔊"
            },
            fontSize = 16.sp
        )

        Text(
            text       = when (event.streamType) {
                StreamType.MUSIC -> "Media"
                StreamType.RING  -> "Ringtone"
                StreamType.CALL  -> "Call"
                else             -> "Volume"
            },
            color      = AtomixColors.TextSecondary,
            fontSize   = 11.sp
        )

        // Volume bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(AtomixColors.SurfaceGlass)
        ) {
            val progress = event.level.toFloat() / event.maxLevel
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AtomixColors.ElectricBlue, AtomixColors.PurpleGlow)
                        )
                    )
            )
        }

        Text(
            text       = event.level.toString(),
            color      = AtomixColors.TextPrimary,
            fontSize   = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ─── Download Island ──────────────────────────────────────────────────────────
@Composable
fun DownloadIsland(event: IslandEvent.Download) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Download icon
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(AtomixColors.ElectricBlue.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("↓", color = AtomixColors.ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text     = event.fileName,
                color    = AtomixColors.TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            // Animated progress
            val animatedProgress by animateFloatAsState(
                targetValue   = event.progress,
                animationSpec = tween(400),
                label         = "dlProgress"
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
                                listOf(AtomixColors.ElectricBlue, AtomixColors.MintGreen)
                            )
                        )
                )
            }
        }

        Text(
            text       = if (event.isComplete) "Done" else "${(event.progress * 100).toInt()}%",
            color      = if (event.isComplete) AtomixColors.MintGreen else AtomixColors.TextSecondary,
            fontSize   = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─── Navigation Island ────────────────────────────────────────────────────────
@Composable
fun NavigationIsland(event: IslandEvent.Navigation) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Direction arrow
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        listOf(AtomixColors.ElectricBlue, AtomixColors.PurpleGlow)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("↑", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = event.instruction,
                color      = AtomixColors.TextPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text  = event.distance,
                color = AtomixColors.ElectricBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (event.eta.isNotEmpty()) {
            Text(
                text     = event.eta,
                color    = AtomixColors.TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
