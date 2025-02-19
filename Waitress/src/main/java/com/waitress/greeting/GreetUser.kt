package com.waitress.greeting

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Date：2024/11/11
 * Describe:
 */
class GreetUser(private val context: Context) {
    private val mIoScope = CoroutineScope(Dispatchers.IO)
    private var mReferrerStr by HostStrCacheImpl()
    private var mConfigure by HostStrCacheImpl(type = 99)

    init {
        startFetchTask()
        mIoScope.launch {
            if (mReferrerStr.isBlank()) {
                delay(18000)
                while (mReferrerStr.isBlank()) {
                    fetchReferrer()
                    delay(15000)
                }
            } else {
                MenuHelper.mMealNetworkHelper.postReferrer(mReferrerStr)
                if (mConfigure.isBlank()) {
                    fetchConfigure()
                } else {
                    MenuHelper.mDishBean.refreshBean(mConfigure)
                    delay(Random.nextLong(1000, 60000 * 10))
                    fetchConfigure()
                }
            }
        }
    }

    fun fetchReferrer() {
        if (mReferrerStr.isNotBlank()) return
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(p0: Int) {
                runCatching {
                    if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        mReferrerStr = response.installReferrer
                        MenuHelper.log("mGoogleReferStr-->${mReferrerStr}")
                        //todo delete
                        if (IS_TEST) {
                            mReferrerStr += "test"
                        }
                        MenuHelper.mMealNetworkHelper.postReferrer(mReferrerStr)
                        fetchConfigure()
                        referrerClient.endConnection()
                    } else {
                        referrerClient.endConnection()
                    }
                }.onFailure {
                    referrerClient.endConnection()
                }
            }

            override fun onInstallReferrerServiceDisconnected() = Unit
        })
    }

    private fun startFetchTask() {
        mIoScope.launch {
            while (true) {
                MenuHelper.mMealNetworkHelper.postEvent("session_up")
                delay(60000 * 10)
                if (System.currentTimeMillis() - lastFetchTime > 60000 * 60) {
                    fetchConfigure()
                }
            }
        }
    }

    private fun fetchCircleTask(num: Int = 10) {
        mIoScope.launch {
            var numAdd = num
            if (num > 10) {
                delay(1000)
            }
            while (numAdd-- > 0) {
                lastFetchTime = 0
                fetchConfigure()
                delay(60000)
                if (MenuHelper.mDishBean.waitressStatus.contains("Server")) {
                    break
                }
            }
        }
    }

    private var lastFetchTime = 0L
    private fun fetchConfigure() {
        if (System.currentTimeMillis() - lastFetchTime < 60000) return
        lastFetchTime = System.currentTimeMillis()
        MenuHelper.mMealNetworkHelper.fetchConfigure(mReferrerStr, success = {
            MenuHelper.log("fetchConfigure--->$it")
            val status = MenuHelper.mDishBean.refreshBean(it)
            if (status == "b") {
                if (mConfigure.isBlank()) {
                    mConfigure = it
                }
                retryRequest()
            } else {
                mConfigure = it
            }
            MenuHelper.mMealNetworkHelper.postEvent("getadmin", Pair("getstring", status))
        }, failed = {
            retryRequest()
        })
    }

    private var num = 6
    private fun retryRequest() {
        if (num <= 0) return
        if (System.currentTimeMillis() - MenuHelper.mDishBean.installTime > 60000 * 12) return
        mIoScope.launch {
            num--
            delay(60000)
            fetchConfigure()
        }
    }

}