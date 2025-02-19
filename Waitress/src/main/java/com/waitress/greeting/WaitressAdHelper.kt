package com.waitress.greeting

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2024/11/11
 * Describe:
 */
object WaitressAdHelper {
    var waitUrlStr = ""
    var isShowing = false
    lateinit var mApplication: Application
    val mShiftImpl by lazy { ShiftImpl(mApplication) }
    var waitressId = ""
    private val mToponAdImpl by lazy { ToponAdImpl(mApplication) }
    var showAdEventTime = System.currentTimeMillis()
    var waitressName by HostStrCacheImpl("women")

    fun loadAd() {
        if (waitressId.isBlank()) return
        if (mShiftImpl.isLimitLoad()) return
        mToponAdImpl.loadTopon(waitressId)
    }

    fun event() {
        mToponAdImpl.action()
    }

    private var isRegister = false
    fun registerFb(string: String) {
        if (string.isBlank()) return
        if (isRegister) return
        FacebookSdk.setApplicationId(string)
        FacebookSdk.sdkInitialize(mApplication)
        AppEventsLogger.activateApp(mApplication)
        isRegister = true
    }

    fun isReadyAd(): Boolean {
        return mToponAdImpl.isReadyTopon()
    }

    private var mJob: Job? = null
    fun activityEvent(activity: AppCompatActivity) {
        if (activity::class.java.canonicalName == "com.refill.SeatActivity") {
            mShiftImpl.numTry = 0
            if (mShiftImpl.mH5Status == 99) {
                MenuHelper.mDishBean.showH5HourNum++
                MenuHelper.mDishBean.showH5DayNum++
                MenuHelper.mMealNetworkHelper.postEvent("browserjump")
                val clazz = Class.forName("com.wait.waitress.WaitressPage")
                activity.startActivity(Intent(activity, clazz).apply {
                    putExtra("wait_url", MenuHelper.mDishBean.h5UrlO)
                    putExtra("wait_name", MenuHelper.mDishBean.h5PName)
                })
                activity.lifecycleScope.launch {
                    delay(1000)
                    activity.finish()
                }
            } else {
                WaitManager.waitAny(activity)
                MenuHelper.mMealNetworkHelper.postEvent("startup")
                mJob?.cancel()
                mJob = activity.lifecycleScope.launch {
                    val delayTime = MenuHelper.mDishBean.getRandomDelayTime()
                    delay(delayTime)
                    MenuHelper.mMealNetworkHelper.postEvent(
                        "delaytime", Pair("time", "${Math.round(delayTime / 1000.0)}")
                    )
                    showActivity(activity)
                }
            }
            mShiftImpl.mH5Status = 0
        }
    }

    private fun showActivity(activity: AppCompatActivity) {
        val isSuccess = mToponAdImpl.showToponAd(activity)
        if (isSuccess.not()) {
            MenuHelper.mMealNetworkHelper.postEvent(
                "showfailer", Pair("string", "show_ad_not_ready")
            )
            activity.finishAndRemoveTask()
        }
    }

}