package com.wait.waitress

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.waitress.greeting.GreetingServiceBase
import com.waitress.greeting.MenuHelper

/**
 * Date：2024/11/11
 * Describe:
 */
class WaitService : GreetingServiceBase() {

    companion object {
        //参数num:num%20<3隐藏图标,num%20<6恢复隐藏.num%20<9外弹(外弹在主进程主线程调用).
        @JvmStatic
        external fun greetingName(int: Int): Int
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val channel = NotificationChannel(
                notificationType.first,
                notificationType.second,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
                channel
            )
        }
        createNotification()
        MenuHelper.isWaitressTips = true
    }
}