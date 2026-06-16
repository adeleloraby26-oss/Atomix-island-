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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.atomix.island.animations.breathingAnimation
import com.atomix.island.animations.glowPulseAnimation
import com.atomix.island.ui.components.IslandEvent
import com.atomix.island.ui.theme.AtomixColors
import java.util.concurrent.TimeUnit

// ─── Incoming Call ────────────────────────────────────────────────────────────
@Composable
fun IncomingCallIsland(event: IslandEvent.IncomingCall) {
    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Caller avatar
        CallerAvatar(photoUrl = event.callerPhoto, name = event.callerName, size = 44.dp)

        // Caller info
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text       = "Incoming Call",
                color      = AtomixColors.MintGreen,
                fontSize   = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Text(
                text       = event.callerName,
                color      = AtomixColors.TextPrimary,
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines   = 1
            )
        }

        // Accept
        PulsingCallButton(color = AtomixColors.MintGreen, isAccept = true, onClick = {})
        // Decline
        CallButton(color = AtomixColors.RoseRed, isAccept = false, onClick = {})
    }
}

// ─── Incoming Call Full ───────────────────────────────────────────────────────
@Composable
fun IncomingCallIslandFull(event: IslandEvent.IncomingCall) {
    Column(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement   = Arrangement.SpaceBetween,
        horizontalAlignment   = Alignment.CenterHorizontally
    ) {
        // Top info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text       = "Incoming Call",
                color      = AtomixColors.MintGreen,
                fontSize   = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            CallerAvatar(photoUrl = event.callerPhoto, name = event.callerName, size = 64.dp)
            Text(
                text       = event.callerName,
                color      = AtomixColors.TextPrimary,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text  = event.callerNumber,
                color = AtomixColors.TextSecondary,
                fontSize = 12.sp
            )
        }

        // Buttons
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CallButton(color = AtomixColors.RoseRed, isAccept = false, size = 48.dp, onClick = {})
                Spacer(Modifier.height(6.dp))
                Text("Decline", color = AtomixColors.RoseRed, fontSize = 11.sp)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PulsingCallButton(color = AtomixColors.MintGreen, isAccept = true, size = 48.dp, onClick = {})
                Spacer(Modifier.height(6.dp))
                Text("Accept", color = AtomixColors.MintGreen, fontSize = 11.sp)
            }
        }
    }
}

// ─── Active Call ──────────────────────────────────────────────────────────────
@Composable
fun ActiveCallIsland(event: IslandEvent.ActiveCall) {
    val glowAlpha = glowPulseAnimation(0.4f, 1f, 1200)

    Row(
        modifier              = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Animated call indicator
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(AtomixColors.MintGreen.copy(alpha = glowAlpha))
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = event.callerName,
                color      = AtomixColors.TextPrimary,
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text     = formatCallDuration(event.duration),
                color    = AtomixColors.MintGreen,
                fontSize = 11.sp
            )
        }

        // Mute toggle
        CallToggleButton(
            active    = event.isMuted,
            activeColor = AtomixColors.RoseRed,
            label     = if (event.isMuted) "Unmute" else "Mute",
            onClick   = {}
        )

        // End call
        CallButton(color = AtomixColors.RoseRed, isAccept = false, size = 32.dp, onClick = {})
    }
}

// ─── Caller Avatar ────────────────────────────────────────────────────────────
@Composable
fun CallerAvatar(photoUrl: String?, name: String, size: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(AtomixColors.PurpleGlow, AtomixColors.ElectricBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl, contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(
                text       = name.firstOrNull()?.uppercase() ?: "?",
                color      = Color.White,
                fontSize   = (size.value * 0.4f).sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ─── Pulsing Call Button ──────────────────────────────────────────────────────
@Composable
fun PulsingCallButton(
    color: Color,
    isAccept: Boolean,
    size: androidx.compose.ui.unit.Dp = 34.dp,
    onClick: () -> Unit,
) {
    val pulseScale by rememberInfiniteTransition(label = "callPulse").animateFloat(
        initialValue  = 1f,
        targetValue   = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "callPulseScale"
    )

    Box(contentAlignment = Alignment.Center) {
        // Pulse ring
        Box(
            modifier = Modifier
                .size(size + 10.dp)
                .scale(pulseScale)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.2f))
        )
        CallButton(color = color, isAccept = isAccept, size = size, onClick = onClick)
    }
}

// ─── Call Button ──────────────────────────────────────────────────────────────
@Composable
fun CallButton(
    color: Color,
    isAccept: Boolean,
    size: androidx.compose.ui.unit.Dp = 34.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Phone icon
        androidx.compose.foundation.Canvas(Modifier.size(size * 0.5f)) {
            val w = this.size.width
            val h = this.size.height
            val path = Path().apply {
                // Simplified phone handset path
                moveTo(w * 0.2f, h * 0.1f)
                cubicTo(w * 0.2f, h * 0.3f, w * 0.1f, h * 0.4f, w * 0.35f, h * 0.55f)
                cubicTo(w * 0.55f, h * 0.7f, w * 0.7f, h * 0.65f, w * 0.85f, h * 0.65f)
                lineTo(w * 0.85f, h * 0.45f)
                lineTo(w * 0.65f, h * 0.4f)
                lineTo(w * 0.6f, h * 0.55f)
                cubicTo(w * 0.45f, h * 0.5f, w * 0.45f, h * 0.35f, w * 0.4f, h * 0.2f)
                lineTo(w * 0.55f, h * 0.15f)
                lineTo(w * 0.5f, h * -0.05f)
                close()
            }
            drawPath(path, Color.White)
        }
    }
}

@Composable
private fun CallToggleButton(
    active: Boolean,
    activeColor: Color,
    label: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(26.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(if (active) activeColor.copy(alpha = 0.25f) else AtomixColors.SurfaceGlass)
            .clickable { onClick() }
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = label,
            color = if (active) activeColor else AtomixColors.TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatCallDuration(seconds: Long): String {
    val m = TimeUnit.SECONDS.toMinutes(seconds)
    val s = seconds - TimeUnit.MINUTES.toSeconds(m)
    return "%02d:%02d".format(m, s)
}
