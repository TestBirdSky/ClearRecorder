package com.wait.waitress.busser

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.waitress.greeting.BusserReceiverHelper

/**
 * Date：2024/11/11
 * Describe:
 */
class BusserBroadcast : BroadcastReceiver() {
    private val mBusserReceiverHelper by lazy { BusserReceiverHelper() }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            mBusserReceiverHelper.action(context, intent)
        }
    }
}