package com.atomix.island.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ─── Atomix Color Palette ───────────────────────────────────────────────────
object AtomixColors {
    val PureBlack       = Color(0xFF000000)
    val DeepSpace       = Color(0xFF0D1117)
    val Graphite        = Color(0xFF1C1F26)
    val GraphiteLight   = Color(0xFF252830)
    val SurfaceGlass    = Color(0x33FFFFFF)   // 20% white
    val SurfaceGlassDim = Color(0x1AFFFFFF)   // 10% white
    val SurfaceBorder   = Color(0x26FFFFFF)   // 15% white

    val ElectricBlue    = Color(0xFF00A3FF)
    val ElectricBlueGlow= Color(0x4000A3FF)
    val PurpleGlow      = Color(0xFF7B61FF)
    val PurpleGlowDim   = Color(0x407B61FF)
    val MintGreen       = Color(0xFF00E5A0)
    val SunriseOrange   = Color(0xFFFF6B35)
    val RoseRed         = Color(0xFFFF3B5C)
    val GoldenAmber     = Color(0xFFFFB800)

    val White           = Color(0xFFFFFFFF)
    val WhiteSoft       = Color(0xCCFFFFFF)   // 80% white
    val WhiteDim        = Color(0x80FFFFFF)   // 50% white
    val WhiteGhost      = Color(0x33FFFFFF)   // 20% white

    val TextPrimary     = Color(0xFFFFFFFF)
    val TextSecondary   = Color(0xB3FFFFFF)   // 70% white
    val TextTertiary    = Color(0x66FFFFFF)   // 40% white

    // Gradient pairs
    val GradientBlueStart  = Color(0xFF00A3FF)
    val GradientBlueEnd    = Color(0xFF0047FF)
    val GradientPurpleStart= Color(0xFF7B61FF)
    val GradientPurpleEnd  = Color(0xFFBB86FC)
    val GradientMusicStart = Color(0xFFFF6B35)
    val GradientMusicEnd   = Color(0xFFFF3B5C)
    val GradientCallStart  = Color(0xFF00E5A0)
    val GradientCallEnd    = Color(0xFF00A3FF)
    val GradientAiStart    = Color(0xFF7B61FF)
    val GradientAiEnd      = Color(0xFF00A3FF)
}

// ─── Custom compositionLocal for Atomix-specific tokens ─────────────────────
@Immutable
data class AtomixExtendedColors(
    val islandBackground: Color,
    val islandBorder: Color,
    val islandGlow: Color,
    val glassLayer1: Color,
    val glassLayer2: Color,
    val accent: Color,
    val accentGlow: Color,
    val onAccent: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
)

val LocalAtomixColors = staticCompositionLocalOf {
    AtomixExtendedColors(
        islandBackground = AtomixColors.PureBlack,
        islandBorder     = AtomixColors.SurfaceBorder,
        islandGlow       = AtomixColors.ElectricBlueGlow,
        glassLayer1      = AtomixColors.SurfaceGlass,
        glassLayer2      = AtomixColors.SurfaceGlassDim,
        accent           = AtomixColors.ElectricBlue,
        accentGlow       = AtomixColors.ElectricBlueGlow,
        onAccent         = AtomixColors.White,
        textPrimary      = AtomixColors.TextPrimary,
        textSecondary    = AtomixColors.TextSecondary,
        textTertiary     = AtomixColors.TextTertiary,
    )
}

// ─── Typography ─────────────────────────────────────────────────────────────
val AtomixTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Thin,   fontSize = 57.sp, letterSpacing = (-0.25).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.Light,  fontSize = 45.sp),
    displaySmall  = TextStyle(fontWeight = FontWeight.Light,  fontSize = 36.sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 32.sp, letterSpacing = (-0.5).sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 28.sp, letterSpacing = (-0.3).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.Medium, fontSize = 24.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 22.sp, letterSpacing = (-0.2).sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.Medium, fontSize = 16.sp, letterSpacing = 0.15.sp),
    titleSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 16.sp, letterSpacing = 0.5.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, letterSpacing = 0.25.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal, fontSize = 12.sp, letterSpacing = 0.4.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 14.sp, letterSpacing = 0.1.sp),
    labelMedium   = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp, letterSpacing = 0.5.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.5.sp),
)

// ─── Dark Color Scheme ───────────────────────────────────────────────────────
private val AtomixDarkColorScheme = darkColorScheme(
    primary          = AtomixColors.ElectricBlue,
    onPrimary        = AtomixColors.White,
    primaryContainer = AtomixColors.Graphite,
    secondary        = AtomixColors.PurpleGlow,
    onSecondary      = AtomixColors.White,
    tertiary         = AtomixColors.MintGreen,
    background       = AtomixColors.PureBlack,
    surface          = AtomixColors.DeepSpace,
    surfaceVariant   = AtomixColors.Graphite,
    onBackground     = AtomixColors.TextPrimary,
    onSurface        = AtomixColors.TextPrimary,
    onSurfaceVariant = AtomixColors.TextSecondary,
    outline          = AtomixColors.SurfaceBorder,
    outlineVariant   = AtomixColors.SurfaceGlassDim,
    error            = AtomixColors.RoseRed,
)

// ─── Theme Composable ────────────────────────────────────────────────────────
@Composable
fun AtomixIslandTheme(
    accentColor: Color = AtomixColors.ElectricBlue,
    content: @Composable () -> Unit
) {
    val extendedColors = AtomixExtendedColors(
        islandBackground = AtomixColors.PureBlack,
        islandBorder     = AtomixColors.SurfaceBorder,
        islandGlow       = accentColor.copy(alpha = 0.25f),
        glassLayer1      = AtomixColors.SurfaceGlass,
        glassLayer2      = AtomixColors.SurfaceGlassDim,
        accent           = accentColor,
        accentGlow       = accentColor.copy(alpha = 0.25f),
        onAccent         = AtomixColors.White,
        textPrimary      = AtomixColors.TextPrimary,
        textSecondary    = AtomixColors.TextSecondary,
        textTertiary     = AtomixColors.TextTertiary,
    )

    CompositionLocalProvider(LocalAtomixColors provides extendedColors) {
        MaterialTheme(
            colorScheme = AtomixDarkColorScheme,
            typography  = AtomixTypography,
            content     = content
        )
    }
}

// Convenience accessor
val MaterialTheme.atomix: AtomixExtendedColors
    @Composable get() = LocalAtomixColors.current
