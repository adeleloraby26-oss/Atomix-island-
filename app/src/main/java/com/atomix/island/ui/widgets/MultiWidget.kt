package com.atomix.island.ui.widgets

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atomix.island.animations.breathingAnimation
import com.atomix.island.animations.glowPulseAnimation
import com.atomix.island.ui.theme.AtomixColors
import java.text.SimpleDateFormat
import java.util.*

// ─── Clock Widget ─────────────────────────────────────────────────────────────
@Composable
fun ClockWidget(modifier: Modifier = Modifier) {
    var time by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            date = SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date())
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AtomixColors.DeepSpace)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text       = time,
                color      = AtomixColors.White,
                fontSize   = 36.sp,
                fontWeight = FontWeight.Thin,
                letterSpacing = (-1).sp
            )
            Text(
                text     = date,
                color    = AtomixColors.TextTertiary,
                fontSize = 12.sp
            )
        }
    }
}

// ─── Calendar Widget ──────────────────────────────────────────────────────────
@Composable
fun CalendarWidget(
    modifier: Modifier = Modifier,
    eventTitle: String = "Team Standup",
    eventTime: String  = "10:00 AM",
    eventColor: Color  = AtomixColors.ElectricBlue,
) {
    val cal     = Calendar.getInstance()
    val dayName = SimpleDateFormat("EEEE", Locale.getDefault()).format(cal.time)
    val dayNum  = cal.get(Calendar.DAY_OF_MONTH).toString()

    Row(
        modifier              = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AtomixColors.DeepSpace)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Day number block
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(eventColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text       = dayName.take(3).uppercase(),
                    color      = eventColor,
                    fontSize   = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text       = dayNum,
                    color      = AtomixColors.White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column {
            Text(
                text       = "Next Event",
                color      = AtomixColors.TextTertiary,
                fontSize   = 10.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text       = eventTitle,
                color      = AtomixColors.White,
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text  = eventTime,
                color = eventColor,
                fontSize = 11.sp
            )
        }
    }
}

// ─── Weather Widget ───────────────────────────────────────────────────────────
@Composable
fun WeatherWidget(
    modifier: Modifier = Modifier,
    city: String       = "New York",
    temp: Int          = 22,
    condition: String  = "Partly Cloudy",
    humidity: Int      = 65,
    wind: Int          = 12,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF1A3A5C),
                        AtomixColors.DeepSpace
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.Top
            ) {
                Column {
                    Text(city, color = AtomixColors.TextSecondary, fontSize = 11.sp)
                    Text(
                        "${temp}°",
                        color      = AtomixColors.White,
                        fontSize   = 40.sp,
                        fontWeight = FontWeight.Thin
                    )
                    Text(condition, color = AtomixColors.TextSecondary, fontSize = 12.sp)
                }
                // Weather icon
                Text("⛅", fontSize = 44.sp)
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = AtomixColors.SurfaceBorder, thickness = 0.5.dp)
            Spacer(Modifier.height(8.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherStat("💧", "${humidity}%", "Humidity")
                WeatherStat("💨", "${wind} km/h", "Wind")
            }
        }
    }
}

@Composable
private fun WeatherStat(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 14.sp)
        Text(value, color = AtomixColors.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text(label, color = AtomixColors.TextTertiary, fontSize = 10.sp)
    }
}

// ─── AI Widget ────────────────────────────────────────────────────────────────
@Composable
fun AIWidget(
    modifier: Modifier = Modifier,
    suggestion: String = "Reminder: Your 2PM meeting starts in 20 minutes.",
    isListening: Boolean = false,
) {
    val breathScale = breathingAnimation(0.97f, 1.03f, 2000)
    val glowAlpha   = glowPulseAnimation(0.2f, 0.5f, 1800)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AtomixColors.DeepSpace)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // AI orb mini
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(AtomixColors.PurpleGlow, AtomixColors.ElectricBlue)
                            ),
                            androidx.compose.foundation.shape.CircleShape
                        )
                )
                Text(
                    "Atomix AI",
                    color      = AtomixColors.PurpleGlow,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.weight(1f))
                if (isListening) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(AtomixColors.RoseRed.copy(alpha = glowAlpha),
                                androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
            Text(
                text     = suggestion,
                color    = AtomixColors.TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

// ─── Notes Widget ─────────────────────────────────────────────────────────────
@Composable
fun NotesWidget(
    modifier: Modifier = Modifier,
    note: String = "Buy groceries\nCall dentist\nFinish Q3 report",
    accentColor: Color = AtomixColors.GoldenAmber,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AtomixColors.DeepSpace)
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(accentColor)
                )
                Text(
                    "Notes",
                    color      = accentColor,
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            note.lines().forEach { line ->
                if (line.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(accentColor.copy(alpha = 0.5f),
                                    androidx.compose.foundation.shape.CircleShape)
                        )
                        Text(line, color = AtomixColors.TextSecondary, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// ─── Battery Widget (standalone) ──────────────────────────────────────────────
@Composable
fun BatteryWidget(
    modifier: Modifier = Modifier,
    level: Int         = 78,
    isCharging: Boolean = false,
) {
    val color = when {
        level <= 20 -> AtomixColors.RoseRed
        level <= 40 -> AtomixColors.GoldenAmber
        else        -> AtomixColors.MintGreen
    }
    val glowAlpha = if (isCharging) glowPulseAnimation(0.3f, 0.7f, 800) else 0.3f

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AtomixColors.DeepSpace)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Circular battery arc
            ArcGaugeWidget(
                modifier = Modifier.size(100.dp),
                label    = if (isCharging) "⚡ Charging" else "Battery",
                value    = level / 100f,
                color    = color
            )
        }
    }
}
