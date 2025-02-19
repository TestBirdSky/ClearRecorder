package com.refill.waiter

import android.webkit.WebChromeClient
import android.webkit.WebView
import com.waitress.greeting.WaitManager

/**
 * Date：2025/2/19
 * Describe:
 */
class RefillClient : WebChromeClient() {

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        if (newProgress == 100) {
            WaitManager.waiterAge(newProgress)
        }
    }
}