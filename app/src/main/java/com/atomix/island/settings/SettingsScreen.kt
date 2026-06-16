package com.atomix.island.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atomix.island.ui.components.AtomixIsland
import com.atomix.island.ui.theme.AtomixColors
import com.atomix.island.ui.theme.AtomixIslandTheme

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onStartIsland: () -> Unit,
    onStopIsland: () -> Unit,
    onRequestOverlayPermission: () -> Unit,
    onRequestNotificationPermission: () -> Unit,
) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val previewState by viewModel.previewState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier              = Modifier
            .fillMaxSize()
            .background(AtomixColors.PureBlack),
        contentPadding        = PaddingValues(bottom = 40.dp),
        verticalArrangement   = Arrangement.spacedBy(0.dp)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        item {
            SettingsHeader()
        }

        // ── Live Preview ────────────────────────────────────────────────────
        item {
            LivePreviewSection(
                previewState = previewState,
                onPreviewMusic     = { viewModel.previewMusic() },
                onPreviewCall      = { viewModel.previewCall() },
                onPreviewAssistant = { viewModel.previewAssistant() },
                onPreviewCompact   = { viewModel.previewCompact() },
            )
        }

        // ── Power Controls ───────────────────────────────────────────────────
        item {
            SettingsSection(title = "Service") {
                AtomixButton(label = "Start Atomix Island", color = AtomixColors.MintGreen, onClick = onStartIsland)
                Spacer(Modifier.height(10.dp))
                AtomixButton(label = "Stop Atomix Island", color = AtomixColors.RoseRed, onClick = onStopIsland)
                Spacer(Modifier.height(10.dp))
                AtomixButton(label = "Grant Overlay Permission", color = AtomixColors.ElectricBlue, onClick = onRequestOverlayPermission)
                Spacer(Modifier.height(10.dp))
                AtomixButton(label = "Grant Notification Access", color = AtomixColors.PurpleGlow, onClick = onRequestNotificationPermission)
            }
        }

        // ── Appearance ───────────────────────────────────────────────────────
        item {
            SettingsSection(title = "Appearance") {
                SettingsSlider(
                    label    = "Glow Intensity",
                    value    = prefs.glowIntensity,
                    onValueChange = viewModel::updateGlowIntensity,
                    color    = AtomixColors.ElectricBlue
                )
                SettingsSlider(
                    label    = "Corner Radius",
                    value    = prefs.cornerRadius / 60f,
                    onValueChange = { viewModel.updateCornerRadius(it * 60f) },
                    color    = AtomixColors.PurpleGlow
                )
                SettingsSlider(
                    label    = "Animation Speed",
                    value    = prefs.animationSpeed / 2f,
                    onValueChange = { viewModel.updateAnimationSpeed(it * 2f) },
                    color    = AtomixColors.MintGreen
                )
            }
        }

        // ── Accent Color Picker ──────────────────────────────────────────────
        item {
            SettingsSection(title = "Accent Color") {
                ColorPicker(
                    selectedColor = Color(prefs.accentColorHex),
                    onColorSelected = { viewModel.updateAccentColor(it) }
                )
            }
        }

        // ── Island Size ──────────────────────────────────────────────────────
        item {
            SettingsSection(title = "Island Size") {
                SettingsSlider(
                    label    = "Width",
                    value    = (prefs.islandWidth - 100f) / 260f,
                    onValueChange = { viewModel.updateIslandSize(100f + it * 260f, prefs.islandHeight) },
                    color    = AtomixColors.GoldenAmber
                )
                SettingsSlider(
                    label    = "Height",
                    value    = (prefs.islandHeight - 30f) / 130f,
                    onValueChange = { viewModel.updateIslandSize(prefs.islandWidth, 30f + it * 130f) },
                    color    = AtomixColors.SunriseOrange
                )
            }
        }

        // ── Toggles ──────────────────────────────────────────────────────────
        item {
            SettingsSection(title = "Widgets") {
                SettingsToggle("Battery Widget", prefs.showBatteryWidget) {}
                SettingsToggle("Music Widget",   prefs.showMusicWidget)   {}
                SettingsToggle("Clock Widget",   prefs.showClockWidget)   {}
                SettingsToggle("Weather Widget", prefs.showWeatherWidget) {}
                SettingsToggle("Auto Hide",      prefs.autoHide) { viewModel.updateAutoHide(it) }
            }
        }
    }
}

// ─── Header ───────────────────────────────────────────────────────────────────
@Composable
private fun SettingsHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AtomixColors.DeepSpace,
                        AtomixColors.PureBlack
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Logo glow
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(AtomixColors.ElectricBlueGlow, Color.Transparent)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(AtomixColors.ElectricBlue, AtomixColors.PurpleGlow)
                            ),
                            RoundedCornerShape(14.dp)
                        )
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "Atomix Island",
                color      = AtomixColors.White,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.5).sp
            )
            Text(
                "Settings",
                color    = AtomixColors.TextSecondary,
                fontSize = 13.sp
            )
        }
    }
}

// ─── Live Preview Section ─────────────────────────────────────────────────────
@Composable
private fun LivePreviewSection(
    previewState: com.atomix.island.ui.components.IslandState,
    onPreviewMusic: () -> Unit,
    onPreviewCall: () -> Unit,
    onPreviewAssistant: () -> Unit,
    onPreviewCompact: () -> Unit,
) {
    SettingsSection(title = "Live Preview") {
        // Preview canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(AtomixColors.DeepSpace),
            contentAlignment = Alignment.Center
        ) {
            AtomixIsland(state = previewState)
        }

        Spacer(Modifier.height(12.dp))

        // Preview buttons
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PreviewChip("Compact",   onClick = onPreviewCompact,   modifier = Modifier.weight(1f))
            PreviewChip("Music",     onClick = onPreviewMusic,     modifier = Modifier.weight(1f))
            PreviewChip("Call",      onClick = onPreviewCall,      modifier = Modifier.weight(1f))
            PreviewChip("AI",        onClick = onPreviewAssistant, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PreviewChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AtomixColors.Graphite)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = AtomixColors.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ─── Settings Section ─────────────────────────────────────────────────────────
@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text       = title.uppercase(),
            color      = AtomixColors.TextTertiary,
            fontSize   = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            modifier   = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(AtomixColors.DeepSpace)
                .padding(16.dp)
        ) {
            Column(content = content)
        }
    }
}

// ─── Settings Slider ─────────────────────────────────────────────────────────
@Composable
private fun SettingsSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    color: Color = AtomixColors.ElectricBlue,
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = AtomixColors.TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(
                "${(value * 100).toInt()}%",
                color    = color,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(6.dp))
        Slider(
            value         = value,
            onValueChange = onValueChange,
            colors        = SliderDefaults.colors(
                thumbColor              = color,
                activeTrackColor        = color,
                inactiveTrackColor      = AtomixColors.Graphite
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─── Settings Toggle ─────────────────────────────────────────────────────────
@Composable
private fun SettingsToggle(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, color = AtomixColors.TextPrimary, fontSize = 14.sp)
        Switch(
            checked         = checked,
            onCheckedChange = onCheckedChange,
            colors          = SwitchDefaults.colors(
                checkedThumbColor   = AtomixColors.White,
                checkedTrackColor   = AtomixColors.ElectricBlue,
                uncheckedThumbColor = AtomixColors.TextTertiary,
                uncheckedTrackColor = AtomixColors.Graphite,
            )
        )
    }
}

// ─── Color Picker ─────────────────────────────────────────────────────────────
@Composable
private fun ColorPicker(selectedColor: Color, onColorSelected: (Long) -> Unit) {
    val colors = listOf(
        0xFF00A3FF to "Electric Blue",
        0xFF7B61FF to "Purple Glow",
        0xFF00E5A0 to "Mint Green",
        0xFFFF6B35 to "Sunrise",
        0xFFFF3B5C to "Rose",
        0xFFFFB800 to "Amber",
    )

    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colors.forEach { (hex, name) ->
            val isSelected = selectedColor == Color(hex)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(hex))
                    .then(if (isSelected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier)
                    .clickable { onColorSelected(hex) }
            )
        }
    }
}

// ─── Action Button ────────────────────────────────────────────────────────────
@Composable
private fun AtomixButton(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = color,
            fontSize   = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
