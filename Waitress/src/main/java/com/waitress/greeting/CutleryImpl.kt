package com.waitress.greeting

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.webkit.WebView
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import java.util.UUID

/**
 * Date：2024/11/8
 * Describe:
 */
class CutleryImpl(private val context: Context) {
    val isCutlery: Boolean

    init {
        isCutlery = getProName() == context.packageName
    }

    fun initCutlery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName: String = Application.getProcessName()
            if (!context.packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    fun refreshData() {
        if (MenuHelper.mAndroidStr.isBlank()) {
            MenuHelper.mAndroidStr =
                Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                    .ifBlank { UUID.randomUUID().toString() }
        }
        context.packageManager.getPackageInfo(context.packageName, 0).apply {
            MenuHelper.mDishBean.installTime = firstInstallTime
            MenuHelper.mDishBean.versionName = versionName
        }
        if (context is Application) {
            MenuHelper.mMealNetworkHelper.mApplication = context
            WaitressAdHelper.mApplication = context
            context.registerActivityLifecycleCallbacks(DessertLifecycleCallback())
        }
        fetchAdjust()
        val mGreetUser = GreetUser(context)
        mGreetUser.fetchReferrer()
    }

    private fun getProName(): String {
        runCatching {
            val am = context.getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = am.runningAppProcesses ?: return ""
            for (info in runningApps) {
                when (info.pid) {
                    android.os.Process.myPid() -> return info.processName
                }
            }
        }
        return ""
    }

    private fun fetchAdjust() {
        // todo modify
        val environment = AdjustConfig.ENVIRONMENT_SANDBOX
//        if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
        // todo modify adjust key
        val config = AdjustConfig(context, "ih2pm2dr3k74", environment)

        Adjust.addSessionCallbackParameter("customer_user_id", MenuHelper.mAndroidStr)

        config.isSendInBackground = true
        config.isFinalAttributionEnabled = true
        config.setOnAttributionChangedListener {
            MenuHelper.log("setOnAttributionChangedListener--->${it.network}")
        }

        Adjust.onCreate(config)
    }

}