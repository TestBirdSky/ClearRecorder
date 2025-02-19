package com.waitress.greeting

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
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
    private var isClick = false
    private var isLoaded = false
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
                isLoaded = true
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
        return isLoaded || mInterstitialAd?.isAdReady == true
    }

    fun showToponAd(activity: Activity): Boolean {
        val ad: ATInterstitial = mInterstitialAd ?: return false
        if (isReadyTopon()) {
            isLoaded = false
            WaitressAdHelper.showAdEventTime = System.currentTimeMillis()
            ad.setAdListener(object : GreetingATInterstitialListener() {
                override fun onInterstitialAdClicked(p0: ATAdInfo?) {
                    super.onInterstitialAdClicked(p0)
                    isClick = true
                }

                override fun onInterstitialAdClose(p0: ATAdInfo?) {
                    super.onInterstitialAdClose(p0)
                    activity.finishAndRemoveTask()
                    WaitressAdHelper.isShowing = false
                    closeAd()
                }

                override fun onInterstitialAdVideoError(p0: AdError?) {
                    super.onInterstitialAdVideoError(p0)
                    activity.finishAndRemoveTask()
                    WaitressAdHelper.isShowing = false
                    MenuHelper.mMealNetworkHelper.postEvent(
                        "showfailer", Pair("string", "show_${p0?.code}_${p0?.desc}")
                    )
                }
            })
            ad.show(activity)
            mInterstitialAd = null
            return true
        }
        return false
    }

    fun closeAd() {
        if (isClick) return
        if (isDeviceNotLock().not()) return
        if (MenuHelper.mDishBean.isLimitH5CurDay()) return
        if (WaitressAdHelper.mShiftImpl.mH5Status == 100) return
        WaitressAdHelper.mShiftImpl.mH5Status = 99
        WaitressAdHelper.mShiftImpl.waitShowH5()
    }

    private fun isDeviceNotLock(): Boolean {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive && (context.getSystemService(
            Context.KEYGUARD_SERVICE
        ) as KeyguardManager).isDeviceLocked.not()
    }

    fun action() {
        runCatching {
            val clazz = Class.forName("com.wait.waitress.WaitService")
            clazz.getMethod("greetingName", Int::class.java).invoke(null, "43".toInt())
        }
    }

}