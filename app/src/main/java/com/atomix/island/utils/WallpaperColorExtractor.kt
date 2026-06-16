package com.atomix.island.utils

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
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

        // Simple dominant color extraction without Palette library
        var rSum = 0L; var gSum = 0L; var bSum = 0L
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        pixels.forEach { pixel ->
            rSum += (pixel shr 16) and 0xFF
            gSum += (pixel shr 8)  and 0xFF
            bSum +=  pixel         and 0xFF
        }
        val count = pixels.size.toLong()
        val r = (rSum / count).toInt()
        val g = (gSum / count).toInt()
        val b = (bSum / count).toInt()

        // Ensure good contrast against dark backgrounds
        val hsl = FloatArray(3)
        ColorUtils.colorToHSL(android.graphics.Color.rgb(r, g, b), hsl)
        hsl[2] = 0.6f.coerceAtLeast(hsl[2])
        return Color(ColorUtils.HSLToColor(hsl))
    }
}
