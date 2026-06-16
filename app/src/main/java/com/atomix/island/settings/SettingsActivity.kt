package com.atomix.island.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.atomix.island.services.IslandOverlayService
import com.atomix.island.ui.theme.AtomixColors
import com.atomix.island.ui.theme.AtomixIslandTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AtomixIslandTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = AtomixColors.PureBlack
                ) {
                    SettingsScreen(
                        viewModel      = viewModel,
                        onStartIsland  = { startIslandService() },
                        onStopIsland   = { stopIslandService() },
                        onRequestOverlayPermission = { requestOverlayPermission() },
                        onRequestNotificationPermission = { requestNotificationPermission() },
                    )
                }
            }
        }
    }

    private fun startIslandService() {
        if (Settings.canDrawOverlays(this)) {
            val intent = Intent(this, IslandOverlayService::class.java)
            startForegroundService(intent)
        } else {
            requestOverlayPermission()
        }
    }

    private fun stopIslandService() {
        stopService(Intent(this, IslandOverlayService::class.java))
    }

    private fun requestOverlayPermission() {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
    }

    private fun requestNotificationPermission() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
}
