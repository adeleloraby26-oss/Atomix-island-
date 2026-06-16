package com.atomix.island.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.atomix.island.ui.theme.AtomixColors
import com.atomix.island.ui.theme.AtomixIslandTheme

class PermissionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AtomixIslandTheme {
                PermissionScreen(
                    hasOverlay       = Settings.canDrawOverlays(this),
                    onGrantOverlay   = { requestOverlay() },
                    onGrantNotif     = { requestNotification() },
                    onDone           = { finish() }
                )
            }
        }
    }

    private fun requestOverlay() {
        startActivity(Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        ))
    }

    private fun requestNotification() {
        startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
}

@Composable
fun PermissionScreen(
    hasOverlay: Boolean,
    onGrantOverlay: () -> Unit,
    onGrantNotif: () -> Unit,
    onDone: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AtomixColors.PureBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(24.dp)
        ) {
            // Title
            Text(
                "Setup Atomix Island",
                color      = AtomixColors.White,
                fontSize   = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Grant the following permissions to activate your Dynamic Island.",
                color    = AtomixColors.TextSecondary,
                fontSize = 14.sp
            )

            // Permission cards
            PermissionCard(
                title       = "Display Over Apps",
                description = "Required to show the floating island above other apps",
                granted     = hasOverlay,
                icon        = "🏝️",
                onGrant     = onGrantOverlay
            )
            PermissionCard(
                title       = "Notification Access",
                description = "Required to show notifications in the island",
                granted     = false,
                icon        = "🔔",
                onGrant     = onGrantNotif
            )

            // Continue button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AtomixColors.ElectricBlue, AtomixColors.PurpleGlow)
                        )
                    )
                    .androidx.compose.foundation.clickable { onDone() }
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Continue", color = AtomixColors.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    icon: String,
    onGrant: () -> Unit,
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(AtomixColors.DeepSpace)
            .padding(16.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(icon, fontSize = 28.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = AtomixColors.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(description, color = AtomixColors.TextTertiary, fontSize = 12.sp)
        }
        if (granted) {
            Text("✓", color = AtomixColors.MintGreen, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(AtomixColors.ElectricBlue.copy(alpha = 0.15f))
                    .androidx.compose.foundation.clickable { onGrant() }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Grant", color = AtomixColors.ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
