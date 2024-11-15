package com.waitress.greeting

import kotlin.reflect.KProperty

/**
 * Date：2024/11/8
 * Describe:
 */
class HostLongCacheImpl(private val def: Long = 0, type: Int = 11) : BaseCache(type) {

    operator fun getValue(any: Any?, name: KProperty<*>): Long {
        if (cacheLong == null) {
            cacheLong = getSp().getLong(getHostKey(name.name), def)
        }
        return cacheLong ?: def
    }

    operator fun setValue(any: Any?, p: KProperty<*>, value: Long) {
        cacheLong = value
        getSp().edit().putLong(getHostKey(p.name), value).apply()
    }
}
