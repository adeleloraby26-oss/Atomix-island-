package com.atomix.island.services

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.AbstractComposeView
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
    private var overlayView: View? = null

    private var islandState by mutableStateOf<IslandState>(IslandState.Compact)
    private var positionX by mutableStateOf(0)
    private var positionY by mutableStateOf(48)

    companion object {
        const val ACTION_UPDATE_STATE = "com.atomix.island.UPDATE_STATE"
        const val EXTRA_ISLAND_STATE  = "island_state"
        var instance: IslandOverlayService? = null
    }

    override fun onCreate() {
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        super.onCreate()
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createOverlay()
        startForeground(1, buildForegroundNotification())
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
        val params = buildLayoutParams()

        val view = object : AbstractComposeView(this) {
            @Composable
            override fun Content() {
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
                                    windowManager.updateViewLayout(this@object, params)
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
        }.also { composeView ->
            composeView.setViewTreeLifecycleOwner(this)
            composeView.setViewTreeViewModelStoreOwner(this)
            composeView.setViewCompositionStrategy(
                androidx.compose.ui.platform.ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
        }

        overlayView = view
        windowManager.addView(view, params)
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
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
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
