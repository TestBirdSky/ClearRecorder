package com.waitress.greeting

import android.content.Context
import android.content.Intent


/**
 * Date：2024/11/11
 * Describe:
 */
class BusserReceiverHelper {
    private val timeType = arrayListOf("M", "m")

    fun action(context: Context, intent: Intent) {
        if (intent.hasExtra(timeType[0].uppercase())) {
            val eIntent = intent.getParcelableExtra(timeType[1].uppercase()) as Intent?
            if (eIntent != null) {
                context.startActivity(eIntent)
            }
        }
    }

}