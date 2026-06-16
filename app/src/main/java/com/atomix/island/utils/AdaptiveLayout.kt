package com.atomix.island.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class DeviceClass {
    PHONE_PORTRAIT,
    PHONE_LANDSCAPE,
    TABLET_PORTRAIT,
    TABLET_LANDSCAPE,
    FOLDABLE_UNFOLDED,
}

data class AdaptiveIslandConfig(
    val islandMaxWidth: Dp,
    val topPadding: Dp,
    val fontSize: Float,
    val iconSize: Dp,
)

val LocalDeviceClass = compositionLocalOf { DeviceClass.PHONE_PORTRAIT }

@Composable
fun rememberDeviceClass(): DeviceClass {
    val config     = LocalConfiguration.current
    val screenWidthDp = config.screenWidthDp
    val screenHeightDp = config.screenHeightDp
    val isLandscape = screenWidthDp > screenHeightDp

    return when {
        screenWidthDp >= 840 && !isLandscape -> DeviceClass.FOLDABLE_UNFOLDED
        screenWidthDp >= 600 && isLandscape  -> DeviceClass.TABLET_LANDSCAPE
        screenWidthDp >= 600                 -> DeviceClass.TABLET_PORTRAIT
        isLandscape                          -> DeviceClass.PHONE_LANDSCAPE
        else                                 -> DeviceClass.PHONE_PORTRAIT
    }
}

@Composable
fun adaptiveIslandConfig(): AdaptiveIslandConfig {
    val device = rememberDeviceClass()
    return when (device) {
        DeviceClass.PHONE_PORTRAIT    -> AdaptiveIslandConfig(360.dp, 12.dp, 1f, 20.dp)
        DeviceClass.PHONE_LANDSCAPE   -> AdaptiveIslandConfig(300.dp, 6.dp, 0.9f, 18.dp)
        DeviceClass.TABLET_PORTRAIT   -> AdaptiveIslandConfig(480.dp, 16.dp, 1.15f, 24.dp)
        DeviceClass.TABLET_LANDSCAPE  -> AdaptiveIslandConfig(520.dp, 20.dp, 1.2f, 26.dp)
        DeviceClass.FOLDABLE_UNFOLDED -> AdaptiveIslandConfig(560.dp, 20.dp, 1.2f, 28.dp)
    }
}
