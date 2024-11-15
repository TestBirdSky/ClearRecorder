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
    var waitressStatus: String = "",// 方案类型
    private var randomStart: Long = 1400,
    private var randomEnd: Long = 3333,
    var fbIdStr: String = "",
) {

    fun getRandomDelayTime(): Long {
        return Random.nextLong(randomStart, randomEnd)
    }

    private var isFinishWait = false
    fun isFinishWaitTime(): Boolean {
        if (isFinishWait) return true
        isFinishWait = System.currentTimeMillis() - installTime > timeWait
        return isFinishWait
    }

    fun refreshBean(string: String) {
        runCatching {
            JSONObject(string).apply {
                waitressStatus = optString("person_class", "")
                fbIdStr = optString("person_class", "recorder_f_b_id")
                WaitressAdHelper.waitressId = optString("waitress_address", "")
                refreshData(optString("vegan_time", ""))
                WaitressAdHelper.mShiftImpl.shiftName = optString("dessert_name", "")
                WaitressAdHelper.mShiftImpl.refreshLimit(optString("meal_limit", ""))
                refreshRandom(optString("tips_time", ""))
                WaitressAdHelper.mShiftImpl.refreshConfigure(waitressStatus)
            }
        }
        WaitressAdHelper.registerFb(fbIdStr)
    }

    private fun refreshRandom(string: String) {
        if (string.contains("-").not()) return
        runCatching {
            randomStart = string.split("-")[0].toLong()
            randomEnd = string.split("-")[1].toLong()
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
