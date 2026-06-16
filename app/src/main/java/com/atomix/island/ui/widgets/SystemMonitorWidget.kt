package com.atomix.island.ui.widgets

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atomix.island.animations.wavePhaseAnimation
import com.atomix.island.ui.theme.AtomixColors
import kotlin.math.cos
import kotlin.math.sin

// ─── System Monitor Dashboard ─────────────────────────────────────────────────
@Composable
fun SystemMonitorWidget(
    cpuPercent: Float     = 0.42f,
    ramPercent: Float     = 0.61f,
    batteryPercent: Float = 0.78f,
    storagePercent: Float = 0.55f,
    temperature: Float    = 38f,
    networkUp: Float      = 0.3f,
    networkDown: Float    = 0.7f,
    fps: Int              = 120,
) {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: CPU + RAM
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArcGaugeWidget(
                modifier = Modifier.weight(1f),
                label    = "CPU",
                value    = cpuPercent,
                color    = AtomixColors.ElectricBlue,
                unit     = "%"
            )
            ArcGaugeWidget(
                modifier = Modifier.weight(1f),
                label    = "RAM",
                value    = ramPercent,
                color    = AtomixColors.PurpleGlow,
                unit     = "%"
            )
        }

        // Row 2: Battery + Storage
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ArcGaugeWidget(
                modifier = Modifier.weight(1f),
                label    = "Battery",
                value    = batteryPercent,
                color    = when {
                    batteryPercent < 0.2f -> AtomixColors.RoseRed
                    batteryPercent < 0.4f -> AtomixColors.GoldenAmber
                    else                  -> AtomixColors.MintGreen
                },
                unit = "%"
            )
            ArcGaugeWidget(
                modifier = Modifier.weight(1f),
                label    = "Storage",
                value    = storagePercent,
                color    = AtomixColors.GoldenAmber,
                unit     = "%"
            )
        }

        // Row 3: Temp + FPS + Network
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatChip(
                modifier = Modifier.weight(1f),
                label    = "TEMP",
                value    = "${temperature.toInt()}°",
                color    = if (temperature > 50f) AtomixColors.RoseRed else AtomixColors.SunriseOrange
            )
            StatChip(
                modifier = Modifier.weight(1f),
                label    = "FPS",
                value    = "$fps",
                color    = AtomixColors.MintGreen
            )
            NetworkWidget(
                modifier     = Modifier.weight(1f),
                upPercent    = networkUp,
                downPercent  = networkDown
            )
        }
    }
}

// ─── Arc Gauge Widget ─────────────────────────────────────────────────────────
@Composable
fun ArcGaugeWidget(
    modifier: Modifier = Modifier,
    label: String,
    value: Float,
    color: Color,
    unit: String = "%",
) {
    val animatedValue by animateFloatAsState(
        targetValue   = value,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label         = "gauge_$label"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(AtomixColors.DeepSpace),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize(0.8f)) {
            val strokeWidth = size.minDimension * 0.08f
            val radius      = (size.minDimension - strokeWidth) / 2f
            val center      = Offset(size.width / 2, size.height / 2)
            val startAngle  = 135f
            val sweepMax    = 270f

            // Track
            drawArc(
                color       = color.copy(alpha = 0.15f),
                startAngle  = startAngle,
                sweepAngle  = sweepMax,
                useCenter   = false,
                topLeft     = Offset(center.x - radius, center.y - radius),
                size        = Size(radius * 2, radius * 2),
                style       = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Fill
            drawArc(
                brush       = Brush.sweepGradient(
                    colors = listOf(color.copy(alpha = 0.6f), color),
                    center = center
                ),
                startAngle  = startAngle,
                sweepAngle  = sweepMax * animatedValue,
                useCenter   = false,
                topLeft     = Offset(center.x - radius, center.y - radius),
                size        = Size(radius * 2, radius * 2),
                style       = Stroke(strokeWidth, cap = StrokeCap.Round)
            )

            // Tip dot
            val tipAngle = Math.toRadians((startAngle + sweepMax * animatedValue).toDouble())
            val tipX = center.x + radius * cos(tipAngle).toFloat()
            val tipY = center.y + radius * sin(tipAngle).toFloat()
            drawCircle(color = color, radius = strokeWidth / 2f, center = Offset(tipX, tipY))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = "${(animatedValue * 100).toInt()}$unit",
                color      = color,
                fontSize   = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text     = label,
                color    = AtomixColors.TextTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─── Stat Chip ────────────────────────────────────────────────────────────────
@Composable
fun StatChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    color: Color,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AtomixColors.DeepSpace)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = AtomixColors.TextTertiary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ─── Network Widget ───────────────────────────────────────────────────────────
@Composable
fun NetworkWidget(
    modifier: Modifier = Modifier,
    upPercent: Float,
    downPercent: Float,
) {
    val phase = wavePhaseAnimation(1000)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AtomixColors.DeepSpace)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(4.dp)
        ) {
            NetworkBar(label = "↑", value = upPercent, color = AtomixColors.MintGreen)
            NetworkBar(label = "↓", value = downPercent, color = AtomixColors.ElectricBlue)
            Text("NET", color = AtomixColors.TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun NetworkBar(label: String, value: Float, color: Color) {
    val animated by animateFloatAsState(
        targetValue   = value,
        animationSpec = tween(600),
        label         = "net_$label"
    )
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animated)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color)
            )
        }
    }
}

// ─── Game Mode Overlay ─────────────────────────────────────────────────────────
@Composable
fun GameModeWidget(
    fps: Int       = 120,
    ping: Int      = 14,
    ramPercent: Float = 0.55f,
    cpuPercent: Float = 0.38f,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AtomixColors.PureBlack.copy(alpha = 0.85f))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        GameStat(value = "$fps", label = "FPS", color = AtomixColors.MintGreen,
            isGood = fps >= 60)
        GameStatDivider()
        GameStat(value = "${ping}ms", label = "PING", color = pingColor(ping),
            isGood = ping < 50)
        GameStatDivider()
        GameStat(value = "${(ramPercent * 100).toInt()}%", label = "RAM", color = AtomixColors.PurpleGlow)
        GameStatDivider()
        GameStat(value = "${(cpuPercent * 100).toInt()}%", label = "CPU", color = AtomixColors.ElectricBlue)
    }
}

@Composable
private fun GameStat(value: String, label: String, color: Color, isGood: Boolean = true) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(label, color = AtomixColors.TextTertiary, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun GameStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(24.dp)
            .background(AtomixColors.SurfaceBorder)
    )
}

private fun pingColor(ping: Int) = when {
    ping < 30  -> AtomixColors.MintGreen
    ping < 80  -> AtomixColors.GoldenAmber
    else       -> AtomixColors.RoseRed
}
