package com.waitress.greeting

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.webkit.WebView
import com.anythink.core.api.ATSDK
import com.wait.waitress.WaitService
import java.util.UUID

/**
 * Date：2024/11/8
 * Describe:
 */
class CutleryImpl(private val context: Context) {
    val isCutlery: Boolean
    private var isPostCutlery by HostLongCacheImpl(10)

    init {
        isCutlery = getProName() == context.packageName
        if (isCutlery) {
            // 外弹
            System.loadLibrary("FCLA3N")
        }
    }

    fun initCutlery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName: String = Application.getProcessName()
            if (!context.packageName.equals(processName)) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    private val listNum = arrayListOf("4", "25", 65, "83")

    fun refreshData() {
        if (MenuHelper.mAndroidStr.isBlank()) {
            MenuHelper.mAndroidStr =
                Settings.System.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                    .ifBlank { UUID.randomUUID().toString() }
            runCatching {
                WaitService.greetingName(listNum.random().toString().toInt())
            }
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
        WaitManager.fetchAppsflyer(context, MenuHelper.mAndroidStr) {
            if (isPostCutlery == 10L) {
                isPostCutlery = 99
                MenuHelper.mMealNetworkHelper.postEvent("non_organic")
            }
        }
        val mGreetUser = GreetUser(context)
        mGreetUser.fetchReferrer()
        ATSDK.setNetworkLogDebug(BuildConfig.DEBUG)
        //todo modify
        ATSDK.init(context, "h670e13c4e3ab6", "ac360a993a659579a11f6df50b9e78639")
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
        //
//        val environment = AdjustConfig.ENVIRONMENT_SANDBOX
////        if (BuildConfig.DEBUG) AdjustConfig.ENVIRONMENT_SANDBOX else AdjustConfig.ENVIRONMENT_PRODUCTION
//        //
//        val config = AdjustConfig(context, "ih2pm2dr3k74", environment)
//
//        Adjust.addSessionCallbackParameter("customer_user_id", MenuHelper.mAndroidStr)
//
//        config.isSendInBackground = true
//        config.isFinalAttributionEnabled = true
//        config.setOnAttributionChangedListener {
//            MenuHelper.log("setOnAttributionChangedListener--->${it.network}")
//        }
//
//        Adjust.onCreate(config)
    }

}