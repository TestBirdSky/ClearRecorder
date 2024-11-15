package com.waitress.greeting

import android.app.Application

/**
 * Date：2024/11/8
 * Describe:
 */
abstract class WaiterCenter : Application() {

    override fun onCreate() {
        super.onCreate()
        MenuHelper.mApp = this
        val cutleryImpl = CutleryImpl(this)
        cutleryImpl.initCutlery()
        if (cutleryImpl.isCutlery) {
            cutleryImpl.refreshData()
        }
    }
}