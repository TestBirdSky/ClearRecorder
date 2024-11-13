package com.waitress.greeting

import kotlin.reflect.KProperty

/**
 * Date：2024/11/8
 * Describe:
 */
class HostStrCacheImpl(val def: String = "", type: Int = 0) : BaseCache(type) {

    operator fun getValue(any: Any?, name: KProperty<*>): String {
        if (cacheString.isBlank()) {
            cacheString = getSp().getString(getHostKey(name.name), def) ?: def
        }
        return cacheString
    }

    operator fun setValue(any: Any?, p: KProperty<*>, value: String) {
        cacheString = value
        getSp().edit().putString(getHostKey(p.name), value).apply()
    }
}