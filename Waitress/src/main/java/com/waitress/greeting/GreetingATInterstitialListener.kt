package com.waitress.greeting

import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialListener
import com.facebook.appevents.AppEventsLogger
import java.lang.String
import java.util.Currency
import kotlin.Pair
import kotlin.let


/**
 * Date：2024/11/13
 * Describe:
 */
open class GreetingATInterstitialListener : ATInterstitialListener {
    override fun onInterstitialAdLoaded() {

    }

    override fun onInterstitialAdLoadFail(p0: AdError?) {
        MenuHelper.mMealNetworkHelper.postEvent(
            "showfailer", Pair("string", "${p0?.code}_${p0?.desc}")
        )
    }

    override fun onInterstitialAdClicked(p0: ATAdInfo?) {
        WaitressAdHelper.mShiftImpl.adClickEvent()
    }

    override fun onInterstitialAdShow(p0: ATAdInfo?) {
        val time = System.currentTimeMillis() - WaitressAdHelper.showAdEventTime
        MenuHelper.mMealNetworkHelper.postEvent("showsuccess", Pair("t", "$time"))
        p0?.let {
            postToponAdValue(it)
            MenuHelper.mMealNetworkHelper.postMealAdValue(it)
        }
        WaitressAdHelper.loadAd()
        WaitressAdHelper.mShiftImpl.adShowEvent()
    }

    override fun onInterstitialAdClose(p0: ATAdInfo?) {

    }

    override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {

    }

    override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {

    }

    override fun onInterstitialAdVideoError(p0: AdError?) {

    }

    private fun postToponAdValue(atAdInfo: ATAdInfo) {
        val adjustAdRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_TOPON)
        adjustAdRevenue.setRevenue(atAdInfo.publisherRevenue, atAdInfo.currency)
        adjustAdRevenue.adRevenueNetwork = String.valueOf(atAdInfo.networkFirmId)
        adjustAdRevenue.adRevenueUnit = atAdInfo.adsourceId
        adjustAdRevenue.adRevenuePlacement = atAdInfo.placementId

        Adjust.trackAdRevenue(adjustAdRevenue)
        runCatching {
            //fb purchase
            AppEventsLogger.newLogger(MenuHelper.mApp).logPurchase(
                (atAdInfo.publisherRevenue).toBigDecimal(), Currency.getInstance("USD")
            )
        }
    }
}