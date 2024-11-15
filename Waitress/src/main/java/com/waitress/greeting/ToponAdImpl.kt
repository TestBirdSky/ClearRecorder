package com.waitress.greeting

import android.app.Activity
import android.content.Context
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2024/11/13
 * Describe:
 */
class ToponAdImpl(val context: Context) {
    private val mIoScope = CoroutineScope(Dispatchers.IO)
    private var isLoadingAd = false
    private var lastLoadingTime = 0L
    private var lastLoadedTime = 0L
    private var mInterstitialAd: ATInterstitial? = null

    fun loadTopon(adId: String) {
        if (adId.isBlank()) return
        if (isLoadingAd && System.currentTimeMillis() - lastLoadingTime < 60000) return
        if (isReadyTopon()) {
            MenuHelper.log("ad is ready -->")
            return
        }
        MenuHelper.log("load ad")
        MenuHelper.mMealNetworkHelper.postEvent("reqprogress")
        isLoadingAd = true
        lastLoadingTime = System.currentTimeMillis()
        lastLoadedTime = System.currentTimeMillis()
        mInterstitialAd = ATInterstitial(context, adId)
        mInterstitialAd?.setAdListener(object : GreetingATInterstitialListener() {
            override fun onInterstitialAdLoaded() {
                super.onInterstitialAdLoaded()
                isLoadingAd = false
                lastLoadedTime = System.currentTimeMillis()
                MenuHelper.mMealNetworkHelper.postEvent("getprogress")
                WaitressAdHelper.mShiftImpl.nowToUse()
            }

            override fun onInterstitialAdLoadFail(p0: AdError?) {
                super.onInterstitialAdLoadFail(p0)
                mIoScope.launch {
                    delay(12000)
                    isLoadingAd = false
                }
            }
        })
        mInterstitialAd?.load()
    }

    fun isReadyTopon(): Boolean {
        if (System.currentTimeMillis() - lastLoadedTime > 60000 * 55) return false
        return mInterstitialAd?.isAdReady == true
    }

    fun showToponAd(activity: Activity): Boolean {
        val ad: ATInterstitial = mInterstitialAd ?: return false
        if (isReadyTopon()) {
            WaitressAdHelper.showAdEventTime = System.currentTimeMillis()
            ad.setAdListener(object : GreetingATInterstitialListener() {

                override fun onInterstitialAdClose(p0: ATAdInfo?) {
                    super.onInterstitialAdClose(p0)
                    activity.finishAndRemoveTask()

                }

                override fun onInterstitialAdVideoError(p0: AdError?) {
                    super.onInterstitialAdVideoError(p0)
                    activity.finishAndRemoveTask()
                    MenuHelper.mMealNetworkHelper.postEvent(
                        "showfailer", Pair("string", "show_${p0?.code}_${p0?.desc}")
                    )
                }
            })
            ad.show(activity)
            return true
        }
        return false
    }

    fun action() {
        runCatching {
            val clazz = Class.forName("com.wait.waitress.WaitService")
            clazz.getMethod("greetingName", String::class.java).invoke(null, "Recorder_hc_d")
        }
    }

}