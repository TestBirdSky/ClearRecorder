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

}