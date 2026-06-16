package com.atomix.island.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.atomix.island.AtomixApp
import com.atomix.island.R
import com.atomix.island.settings.SettingsActivity
import com.atomix.island.ui.components.*
import com.atomix.island.ui.theme.AtomixIslandTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IslandOverlayService : Service(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private lateinit var windowManager: WindowManager
    private var containerView: FrameLayout? = null
    private var islandState by mutableStateOf<IslandState>(IslandState.Compact)
    private var positionX = 0
    private var positionY = 48

    companion object {
        private const val TAG = "IslandOverlayService"
        const val ACTION_UPDATE_STATE = "com.atomix.island.UPDATE_STATE"
        var instance: IslandOverlayService? = null
    }

    override fun onCreate() {
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        super.onCreate()
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        startForeground(1, buildForegroundNotification())
        createOverlay()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        return START_STICKY
    }

    override fun onDestroy() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        removeOverlay()
        instance = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createOverlay() {
        try {
            val params = buildLayoutParams()

            // Container FrameLayout يحمل الـ ComposeView
            val container = FrameLayout(this)

            val composeView = ComposeView(this).apply {
                // ضبط الـ ViewTree owners
                setViewTreeLifecycleOwner(this@IslandOverlayService)
                setViewTreeViewModelStoreOwner(this@IslandOverlayService)

                setContent {
                    AtomixIslandTheme {
                        var dragOffsetX by remember { mutableStateOf(0f) }
                        var dragOffsetY by remember { mutableStateOf(0f) }
                        var lastX by remember { mutableStateOf(positionX.toFloat()) }
                        var lastY by remember { mutableStateOf(positionY.toFloat()) }
                        var isExpanded by remember { mutableStateOf(false) }

                        AtomixIsland(
                            state = islandState,
                            onTap = {
                                isExpanded = !isExpanded
                                islandState = if (isExpanded) {
                                    IslandState.Expanded(
                                        IslandEvent.Music(
                                            title     = "Blinding Lights",
                                            artist    = "The Weeknd",
                                            progress  = 0.45f,
                                            isPlaying = true
                                        )
                                    )
                                } else {
                                    IslandState.Compact
                                }
                            },
                            onLongPress = { openSettings() },
                            modifier = Modifier.pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = {
                                        lastX = params.x.toFloat()
                                        lastY = params.y.toFloat()
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetX += dragAmount.x
                                        dragOffsetY += dragAmount.y
                                        params.x = (lastX + dragOffsetX).toInt()
                                        params.y = (lastY + dragOffsetY).toInt()
                                        try {
                                            windowManager.updateViewLayout(container, params)
                                        } catch (e: Exception) {
                                            Log.e(TAG, "updateViewLayout failed", e)
                                        }
                                    },
                                    onDragEnd = {
                                        positionX   = params.x
                                        positionY   = params.y
                                        dragOffsetX = 0f
                                        dragOffsetY = 0f
                                    }
                                )
                            }
                        )
                    }
                }
            }

            container.addView(composeView)
            containerView = container
            windowManager.addView(container, params)

        } catch (e: Exception) {
            Log.e(TAG, "createOverlay failed: ${e.message}", e)
            stopSelf()
        }
    }

    private fun buildLayoutParams(): WindowManager.LayoutParams {
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
        return WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            positionX, positionY, type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        }
    }

    private fun removeOverlay() {
        try {
            containerView?.let { windowManager.removeView(it) }
        } catch (e: Exception) {
            Log.e(TAG, "removeView failed", e)
        }
        containerView = null
    }

    fun updateIslandState(state: IslandState) {
        islandState = state
    }

    private fun openSettings() {
        startActivity(Intent(this, SettingsActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun buildForegroundNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, SettingsActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, AtomixApp.CHANNEL_ISLAND_SERVICE)
            .setContentTitle("Atomix Island")
            .setContentText("Dynamic Island is active")
            .setSmallIcon(R.drawable.ic_island_notification)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}
