package com.atomix.island.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.ui.graphics.Color
import com.atomix.island.ui.components.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class AtomixNotificationService : NotificationListenerService() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    companion object {
        private val _notificationFlow = MutableSharedFlow<IslandEvent.Notification>(replay = 1)
        val notificationFlow = _notificationFlow.asSharedFlow()

        // Known app accent colors
        private val appColors = mapOf(
            "com.whatsapp"           to Color(0xFF25D366),
            "org.telegram.messenger" to Color(0xFF2AABEE),
            "com.facebook.orca"     to Color(0xFF0084FF),
            "com.instagram.android" to Color(0xFFE1306C),
            "com.discord"           to Color(0xFF5865F2),
            "com.google.android.apps.messaging" to Color(0xFF1A73E8),
        )
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg     = sbn.packageName
        val extras  = sbn.notification.extras
        val title   = extras.getString("android.title") ?: ""
        val text    = extras.getCharSequence("android.text")?.toString() ?: ""
        val appName = getAppName(pkg)

        if (title.isEmpty() && text.isEmpty()) return

        val priority = when (sbn.notification.priority) {
            android.app.Notification.PRIORITY_HIGH, android.app.Notification.PRIORITY_MAX ->
                NotificationPriority.HIGH
            android.app.Notification.PRIORITY_LOW ->
                NotificationPriority.LOW
            else -> NotificationPriority.NORMAL
        }

        val event = IslandEvent.Notification(
            appName     = appName,
            appPackage  = pkg,
            title       = title,
            text        = text,
            priority    = priority,
            accentColor = appColors[pkg] ?: Color(0xFF00A3FF)
        )

        scope.launch {
            _notificationFlow.emit(event)
            // Show in island
            IslandOverlayService.instance?.updateIslandState(IslandState.Expanded(event))
            // Auto-collapse after 4 seconds
            delay(4000)
            IslandOverlayService.instance?.updateIslandState(IslandState.Compact)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {}

    private fun getAppName(packageName: String): String {
        return try {
            val pm   = packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(info).toString()
        } catch (e: Exception) {
            packageName.substringAfterLast(".")
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
