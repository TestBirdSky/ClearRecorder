package com.waitress.greeting

import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitialListener
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.facebook.appevents.AppEventsLogger
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
        WaitressAdHelper.isShowing = true
        val time = System.currentTimeMillis() - WaitressAdHelper.showAdEventTime
        MenuHelper.mMealNetworkHelper.postEvent(
            "showsuccess", Pair("t", "${Math.round(time / 1000.0)}")
        )
        p0?.let {
            postToponAdValue(it)
            MenuHelper.mMealNetworkHelper.postMealAdValue(it)
        }
        WaitressAdHelper.mShiftImpl.adShowEvent()
    }

    override fun onInterstitialAdClose(p0: ATAdInfo?) {
        WaitressAdHelper.loadAd()
    }

    override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {

    }

    override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {

    }

    override fun onInterstitialAdVideoError(p0: AdError?) {

    }

    private fun postToponAdValue(atAdInfo: ATAdInfo) {
        val adRevenueData = AFAdRevenueData(
            atAdInfo.networkName,  // monetizationNetwork
            MediationNetwork.TOPON,  // mediationNetwork
            "USD",  // currencyIso4217Code
            atAdInfo.publisherRevenue // revenue
        )

        val additionalParameters: MutableMap<String, Any> = HashMap()
        additionalParameters[AdRevenueScheme.COUNTRY] = atAdInfo.country
        additionalParameters[AdRevenueScheme.AD_UNIT] = atAdInfo.adsourceId
        additionalParameters[AdRevenueScheme.AD_TYPE] = "record_interstitial"
        additionalParameters[AdRevenueScheme.PLACEMENT] = atAdInfo.placementId
        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)
        runCatching {
            //fb purchase
            AppEventsLogger.newLogger(MenuHelper.mApp).logPurchase(
                (atAdInfo.publisherRevenue).toBigDecimal(), Currency.getInstance("USD")
            )
        }
    }
}