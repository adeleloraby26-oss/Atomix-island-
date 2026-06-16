package com.atomix.island.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "atomix_prefs")

data class IslandPreferences(
    val islandWidth:        Float = 340f,
    val islandHeight:       Float = 82f,
    val positionX:          Int   = 0,
    val positionY:          Int   = 48,
    val transparency:       Float = 0.04f,
    val cornerRadius:       Float = 44f,
    val blurAmount:         Float = 20f,
    val accentColorHex:     Long  = 0xFF00A3FF,
    val animationSpeed:     Float = 1f,
    val glowIntensity:      Float = 0.35f,
    val shadowIntensity:    Float = 0.5f,
    val autoHide:           Boolean = false,
    val autoHideDelay:      Int   = 5000,
    val gestureSensitivity: Float = 0.5f,
    val showBatteryWidget:  Boolean = true,
    val showMusicWidget:    Boolean = true,
    val showClockWidget:    Boolean = true,
    val showWeatherWidget:  Boolean = false,
    val compactWidth:       Float = 126f,
    val compactHeight:      Float = 34f,
)

@Singleton
class IslandPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val store = context.dataStore

    object Keys {
        val ISLAND_WIDTH        = floatPreferencesKey("island_width")
        val ISLAND_HEIGHT       = floatPreferencesKey("island_height")
        val POSITION_X          = intPreferencesKey("position_x")
        val POSITION_Y          = intPreferencesKey("position_y")
        val TRANSPARENCY        = floatPreferencesKey("transparency")
        val CORNER_RADIUS       = floatPreferencesKey("corner_radius")
        val BLUR_AMOUNT         = floatPreferencesKey("blur_amount")
        val ACCENT_COLOR        = longPreferencesKey("accent_color")
        val ANIMATION_SPEED     = floatPreferencesKey("animation_speed")
        val GLOW_INTENSITY      = floatPreferencesKey("glow_intensity")
        val SHADOW_INTENSITY    = floatPreferencesKey("shadow_intensity")
        val AUTO_HIDE           = booleanPreferencesKey("auto_hide")
        val AUTO_HIDE_DELAY     = intPreferencesKey("auto_hide_delay")
        val GESTURE_SENSITIVITY = floatPreferencesKey("gesture_sensitivity")
        val SHOW_BATTERY        = booleanPreferencesKey("show_battery")
        val SHOW_MUSIC          = booleanPreferencesKey("show_music")
        val SHOW_CLOCK          = booleanPreferencesKey("show_clock")
        val SHOW_WEATHER        = booleanPreferencesKey("show_weather")
    }

    val preferences: Flow<IslandPreferences> = store.data.map { prefs ->
        IslandPreferences(
            islandWidth        = prefs[Keys.ISLAND_WIDTH]        ?: 340f,
            islandHeight       = prefs[Keys.ISLAND_HEIGHT]       ?: 82f,
            positionX          = prefs[Keys.POSITION_X]          ?: 0,
            positionY          = prefs[Keys.POSITION_Y]          ?: 48,
            transparency       = prefs[Keys.TRANSPARENCY]        ?: 0.04f,
            cornerRadius       = prefs[Keys.CORNER_RADIUS]       ?: 44f,
            blurAmount         = prefs[Keys.BLUR_AMOUNT]         ?: 20f,
            accentColorHex     = prefs[Keys.ACCENT_COLOR]        ?: 0xFF00A3FF,
            animationSpeed     = prefs[Keys.ANIMATION_SPEED]     ?: 1f,
            glowIntensity      = prefs[Keys.GLOW_INTENSITY]      ?: 0.35f,
            shadowIntensity    = prefs[Keys.SHADOW_INTENSITY]    ?: 0.5f,
            autoHide           = prefs[Keys.AUTO_HIDE]           ?: false,
            autoHideDelay      = prefs[Keys.AUTO_HIDE_DELAY]     ?: 5000,
            gestureSensitivity = prefs[Keys.GESTURE_SENSITIVITY] ?: 0.5f,
            showBatteryWidget  = prefs[Keys.SHOW_BATTERY]        ?: true,
            showMusicWidget    = prefs[Keys.SHOW_MUSIC]          ?: true,
            showClockWidget    = prefs[Keys.SHOW_CLOCK]          ?: true,
            showWeatherWidget  = prefs[Keys.SHOW_WEATHER]        ?: false,
        )
    }

    suspend fun update(block: suspend MutablePreferences.() -> Unit) {
        store.edit { block(it) }
    }

    suspend fun setAccentColor(colorLong: Long) {
        store.edit { it[Keys.ACCENT_COLOR] = colorLong }
    }

    suspend fun setGlowIntensity(v: Float) {
        store.edit { it[Keys.GLOW_INTENSITY] = v }
    }

    suspend fun setAnimationSpeed(v: Float) {
        store.edit { it[Keys.ANIMATION_SPEED] = v }
    }
}
