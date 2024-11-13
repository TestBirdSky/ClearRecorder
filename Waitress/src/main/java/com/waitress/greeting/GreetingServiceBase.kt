package com.waitress.greeting

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat

/**
 * Date：2024/11/11
 * Describe:
 */
abstract class GreetingServiceBase : Service() {
    private var mNotification: Notification? = null

    protected var notificationType = Pair("Notification", "Notification Channel")

    protected fun createNotification() {
        mNotification =
            NotificationCompat.Builder(this, "Notification")
                .setAutoCancel(false).setContentText("")
                .setSmallIcon(R.drawable.ic_greeting_wait).setOngoing(true).setOnlyAlertOnce(true)
                .setContentTitle("")
                .setCustomContentView(RemoteViews(packageName, R.layout.greeting_layout_menu))
                .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        runCatching {
            startForeground(1939, mNotification)
        }
        return START_STICKY
    }

}