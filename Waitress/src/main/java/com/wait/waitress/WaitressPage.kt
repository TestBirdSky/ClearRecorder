package com.wait.waitress

import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.waitress.greeting.MenuHelper
import com.waitress.greeting.databinding.LayoutGreetingPalyBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Date：2025/2/19
 * Describe:
 */
class WaitressPage : AppCompatActivity() {
    private var isFinish = false
    private val viewBinding by lazy { LayoutGreetingPalyBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        val url = intent.getStringExtra("wait_url") ?: ""
        if (url.isBlank()) {
            finish()
            return
        }
        val name = intent.getStringExtra("wait_name") ?: ""
        MenuHelper.log("-->$url --$name")
        viewBinding.ivClose.setOnClickListener {
            MenuHelper.mMealNetworkHelper.postEvent("closebrowser")
            finishAndRemoveTask()
        }

        viewBinding.webView.settings.javaScriptEnabled = true
        viewBinding.webView.settings.userAgentString += "/$name"

        viewBinding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                if (newProgress > 73) {
                    isFinish = true
                }
            }
        }
        lifecycleScope.launch {
            delay(500)
            withTimeoutOrNull(3000) {
                while (isFinish.not()) {
                    delay(300)
                }
            }
            viewBinding.layoutLoading.visibility = View.GONE
            viewBinding.ivClose.visibility = View.VISIBLE
        }
        viewBinding.webView.loadUrl(url)
    }

    override fun onDestroy() {
        viewBinding.webView.destroy()
        super.onDestroy()
    }

}