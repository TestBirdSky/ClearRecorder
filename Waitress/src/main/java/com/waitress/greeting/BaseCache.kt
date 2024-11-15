package com.waitress.greeting

import android.content.SharedPreferences

/**
 * Date：2024/11/8
 * Describe:
 */
abstract class BaseCache(private val type: Int) {
    protected var cacheString: String = ""
    protected var cacheInt: Int? = null
    protected var cacheLong: Long? = null

    fun getSp(): SharedPreferences {
        return when (type) {
            100 -> MenuHelper.mSharedPreferences
            else -> MenuHelper.mEncryptedSharePreferences
        }
    }

    fun getHostKey(name: String): String {
        return when (type) {
            100 -> "Bill_$name"
            else -> {
                name
            }
        }
    }
}