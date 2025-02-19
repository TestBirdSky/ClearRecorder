package com.waitress.greeting

import org.json.JSONObject
import kotlin.random.Random

/**
 * Date：2024/11/8
 * Describe:
 */
data class DishBean(
    var versionName: String = "1.0.1",
    var installTime: Long = 0,
    var timeCheckWaitress: Long = 60000,
    var timePeriodAd: Long = 60000,
    var timeWait: Long = 60000,

    private var randomStart: Long = 1400,
    private var randomEnd: Long = 3333,
    var fbIdStr: String = "",
    var h5UrlO: String = "",
    var h5PName: String = "",
    var timeStart: Long = 0,
) {

    var waitressStatus by HostStrCacheImpl()// 方案类型

    var isCloseService = false

    private var maxHourH5 = 0
    private var maxDayH5 = 0

    var showH5HourNum by HostLongCacheImpl()
    var showH5DayNum by HostLongCacheImpl()

    fun isAllowInH5(): Boolean {
        if (isLimitH5CurDay()) return false
        if (System.currentTimeMillis() - installTime > timeStart) return false
        return true
    }

    fun isLimitH5CurDay(): Boolean {
        if (h5UrlO.isBlank()) return true
        if (maxHourH5 <= showH5HourNum || maxDayH5 <= showH5DayNum) return true
        return false
    }

    fun getRandomDelayTime(): Long {
        return Random.nextLong(randomStart, randomEnd)
    }

    private var isFinishWait = false
    fun isFinishWaitTime(): Boolean {
        if (isFinishWait) return true
        isFinishWait = System.currentTimeMillis() - installTime > timeWait
        return isFinishWait
    }

    fun refreshBean(string: String): String {
        var status = "null"
        runCatching {
            JSONObject(string).apply {
                val str = optString("person_class", "")
                isCloseService = optBoolean("is_destroy_service", false)
                if (str.contains("Server")) {// A 方案
                    status = "a"
                } else if (str.contains("Busser")) {//B 方案
                    status = "b"
                    if (waitressStatus.contains("Server")) {
                        return status
                    }
                }
                waitressStatus = str
                fbIdStr = optString("recorder_f_b_id", "")
                WaitressAdHelper.waitressId = optString("waitress_address", "")
                WaitressAdHelper.waitUrlStr = optString("recorder_url", "")
                h5UrlO = optString("recorder_w_url", "")
                h5PName = optString("recorder_tips_name", "")
                timeStart = optInt("dessert_time_first", 0) * 1000 + timeWait
                refreshH5(optString("vegan_limit"))
                refreshData(optString("vegan_time", ""))
                WaitressAdHelper.mShiftImpl.shiftName = optString("dessert_name", "")
                WaitressAdHelper.mShiftImpl.refreshLimit(optString("meal_limit", ""))
                refreshRandom(optString("tips_time", ""))
                WaitressAdHelper.mShiftImpl.refreshConfigure(waitressStatus)
            }
        }
        WaitressAdHelper.registerFb(fbIdStr)
        return status
    }

    private fun refreshRandom(string: String) {
        if (string.contains("-").not()) return
        runCatching {
            randomStart = string.split("-")[0].toLong()
            randomEnd = string.split("-")[1].toLong()
        }
    }

    private fun refreshH5(string: String) {
        if (string.contains("-").not()) return
        runCatching {
            maxHourH5 = string.split("-")[0].toInt()
            maxDayH5 = string.split("-")[1].toInt()
        }
    }

    private fun refreshData(string: String) {
        if (string.contains("&").not()) return
        runCatching {
            val list = string.split("&")
            timeCheckWaitress = list[0].toInt() * 1000L
            timePeriodAd = list[1].toInt() * 1000L
            timeWait = list[2].toInt() * 1000L
        }
    }

}
