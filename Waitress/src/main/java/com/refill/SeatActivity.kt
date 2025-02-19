package com.refill

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity

/**
 * Date：2024/11/11
 * Describe:
 */
class SeatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback { }
    }

    override fun onDestroy() {
        (this.getWindow().getDecorView() as ViewGroup).removeAllViews()
        super.onDestroy()
    }

}