package com.atomix.island.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import com.atomix.island.ui.theme.AtomixColors
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WallpaperColorExtractor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Extracts the dominant vibrant color from the current wallpaper.
     * Falls back to ElectricBlue if extraction fails or permission denied.
     */
    suspend fun extractDominantColor(): Color = withContext(Dispatchers.IO) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                extractFromWallpaperColorsApi()
            } else {
                extractFromBitmap()
            }
        } catch (e: Exception) {
            AtomixColors.ElectricBlue
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun extractFromWallpaperColorsApi(): Color {
        val wm     = WallpaperManager.getInstance(context)
        val colors = wm.getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
        return colors?.primaryColor?.let {
            Color(it.toArgb())
        } ?: AtomixColors.ElectricBlue
    }

    private fun extractFromBitmap(): Color {
        val wm = WallpaperManager.getInstance(context)
        val drawable = wm.drawable as? BitmapDrawable ?: return AtomixColors.ElectricBlue
        val bitmap   = Bitmap.createScaledBitmap(drawable.bitmap, 64, 64, true)

        val palette = Palette.from(bitmap).generate()
        val swatch  = palette.vibrantSwatch
            ?: palette.lightVibrantSwatch
            ?: palette.dominantSwatch
            ?: return AtomixColors.ElectricBlue

        // Ensure good contrast against dark backgrounds
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(swatch.rgb, hsl)
        hsl[2] = 0.6f.coerceAtLeast(hsl[2]) // min lightness 60%
        return Color(ColorUtils.HSLToColor(hsl))
    }
}
