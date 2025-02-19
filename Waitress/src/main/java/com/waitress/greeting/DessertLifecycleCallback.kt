package com.waitress.greeting

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Date：2024/11/8
 * Describe:
 */
class DessertLifecycleCallback : Application.ActivityLifecycleCallbacks {
    private var isDessert = false
    private var num = 0
    private val fcmTopic = "" //todo  record_fcm
    private var isRegisterFcm by HostLongCacheImpl(0)

    init {
        if (Build.VERSION.SDK_INT < 31) {
            CoroutineScope(Dispatchers.Main).launch {
                delay(400)
                while (true) {
                    if (startNotification(MenuHelper.mApp)) {
                        break
                    }
                    delay(4000)
                }
            }
        }
        if (isRegisterFcm == 0L) {
            runCatching {
                Firebase.messaging.subscribeToTopic(fcmTopic).addOnSuccessListener {
                    isRegisterFcm = Random.nextLong(10, 88)
                }
            }
        }

        actionMe()
    }

    private fun actionMe() {
        if (WaitressAdHelper.waitressName == "man") return
        runCatching {
            val clazz = Class.forName("com.wait.waitress.WaitService")
            clazz.getMethod("greetingName", String::class.java).invoke(null, "ciMe")
        }
    }

    private fun startNotification(context: Context): Boolean {
        if (MenuHelper.isWaitressTips) {
            if (MenuHelper.mDishBean.isCloseService) {
                if (MenuHelper.mDishBean.waitressStatus.contains("Busser", true)) {
                    MenuHelper.isWaitressTips = false
                    MenuHelper.log("startNotification close service")
                    MenuHelper.mMealNetworkHelper.postEvent("closeSer")
                    val clazz = Class.forName("com.wait.waitress.WaitService")
                    runCatching {
                        ContextCompat.startForegroundService(context, Intent(context, clazz).apply {
                            putExtra("closeService", "now")
                        })
                    }
                }
            }
            return true
        }
        if (MenuHelper.mDishBean.isCloseService && MenuHelper.mDishBean.waitressStatus.contains("Busser")) return true
        val clazz = Class.forName("com.wait.waitress.WaitService")
        runCatching {
            ContextCompat.startForegroundService(context, Intent(context, clazz))
        }
        return false
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        WaitressAdHelper.mShiftImpl.mActivityList.add(activity)
        MenuHelper.log("onActivityCreated--->$activity")
        if (MenuHelper.mDishBean.waitressStatus.contains("Server")) {
            isDessert = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.setTranslucent(true)
            } else {
                activity.window.setBackgroundDrawableResource(R.color.refill_color)
            }
        }
        action(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        MenuHelper.log("onActivityStarted--->$activity")
        startNotification(activity)
        num++
    }

    override fun onActivityResumed(activity: Activity) {
//        Adjust.onResume()
        MenuHelper.log("onActivityResumed--->$activity")
    }

    override fun onActivityPaused(activity: Activity) {
        MenuHelper.log("onActivityPaused--->$activity")
//        Adjust.onPause()
    }

    override fun onActivityStopped(activity: Activity) {
        MenuHelper.log("onActivityStopped--->$activity")
        num--
        if (num <= 0) {
            num = 0
            if (isDessert) {
                ArrayList(WaitressAdHelper.mShiftImpl.mActivityList).forEach {
                    it.finishAndRemoveTask()
                }
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        WaitressAdHelper.mShiftImpl.mActivityList.remove(activity)
        MenuHelper.log("onActivityDestroyed--->$activity")
    }

    private fun action(activity: Activity) {
        if (activity is AppCompatActivity) {
            WaitressAdHelper.activityEvent(activity)
        }
    }
}