package com.waitress.greeting

import android.os.Handler
import android.os.Message

/**
 * Date：2025/2/19
 * Describe:
 */
class GreetHelper : Handler() {

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        WaitManager.waiterAge(msg.what)
    }
}