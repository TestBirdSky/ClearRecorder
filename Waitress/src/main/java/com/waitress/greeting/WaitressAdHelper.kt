package com.waitress.greeting

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2024/11/11
 * Describe:
 */
object WaitressAdHelper {
    lateinit var mApplication: Application
    val mShiftImpl by lazy { ShiftImpl(mApplication) }
    var waitressId = ""
    private var isLoading = false
    private var lastLoadingTime: Long = 0

    fun loadAd() {

    }

    fun isReadyAd(): Boolean {
        return false
    }

    private var mJob: Job? = null
    fun activityEvent(activity: AppCompatActivity) {
        if (activity::class.java.canonicalName == "com.refill.SeatActivity") {
            mShiftImpl.numTry = 0
            MenuHelper.mMealNetworkHelper.postEvent("startup")
            mJob?.cancel()
            mJob = activity.lifecycleScope.launch {
                delay(MenuHelper.mDishBean.getRandomDelayTime())
                showActivity(activity)
            }
        }
    }

    private fun showActivity(activity: AppCompatActivity) {
        if (isReadyAd()) {

        } else {
            activity.finishAndRemoveTask()
        }
    }

}