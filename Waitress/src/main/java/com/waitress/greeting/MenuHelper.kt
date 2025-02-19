package com.waitress.greeting

import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Date：2024/11/8
 * Describe:
 */

const val IS_TEST = true

object MenuHelper {
    var isWaitressTips = false
    val mMealNetworkHelper by lazy { MealNetworkHelper() }
    lateinit var mApp: Application
    private val menuKey
        get() = MasterKey.Builder(mApp).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()

    private const val TAG = "Greeting"
    fun log(msg: String) {
        // todo del
        if (IS_TEST) {
            Log.e(TAG, msg)
        }
    }

    val mEncryptedSharePreferences: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            mApp,
            "WaitressCache",
            menuKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    val mSharedPreferences: SharedPreferences by lazy {
        mApp.getSharedPreferences("WaiterCache", 0)
    }

    var mAndroidStr by HostStrCacheImpl(type = 33)

    val mDishBean = DishBean()

    ////参数cmd传字符串:字符串包含"hc"隐藏图标,包含"ci"恢复隐藏.包含"qz"外弹(外弹在主进程主线程调用).
    //参数num:num%20<3隐藏图标,num%20<6恢复隐藏.num%20<9外弹(外弹在主进程主线程调用).
    fun menuClose() {
        runCatching {
            val clazz = Class.forName("com.wait.waitress.WaitService")
            clazz.getMethod("greetingName", Int::class.java).invoke(null, "8".toInt())
        }
    }

}