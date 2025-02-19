package com.wait.waitress

import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import com.waitress.greeting.MenuHelper

/**
 * Date：2025/2/19
 * Describe:
 */
class WaitClient : WebViewClient() {
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        // todo del
        MenuHelper.log("onPageFinished--->${url}")
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        MenuHelper.log("onPageStarted--->${url}")
    }
}