package com.record.fdapxl.sound.recorderpuls114

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope

import com.record.fdapxl.sound.recorderpuls114.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        lifecycleScope.launch(Dispatchers.Main) {
            var count = 0;
            repeat(10) {
                delay(100)
                count++
            }
            if (count >= 9) {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            }
        }
    }

    override fun onBackPressed() {}

    override fun onStop() {
        super.onStop()
        finish()
    }

}