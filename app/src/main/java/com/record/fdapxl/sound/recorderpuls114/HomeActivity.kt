package com.record.fdapxl.sound.recorderpuls114

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.record.fdapxl.sound.recorderpuls114.databinding.ActivityHomeBinding
import com.waitress.greeting.WaitressAdHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    lateinit var ca: ControllerA
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        ca = ControllerA(this, binding);
        val cb = ControllerB(this, binding);
        ControllerC(this, binding);
        binding.navA.setOnClickListener {
            if (seletcIndex == 0) {
            } else {
                seletcIndex = 0;
                binding.navA.alpha = 1.0f;
                binding.navB.alpha = 0.7f;
                binding.navC.alpha = 0.7f;
                binding.pgA.visibility = View.VISIBLE
                binding.pgB.visibility = View.GONE
                binding.pgC.visibility = View.GONE
            }
        }
        binding.navB.setOnClickListener {
            if (seletcIndex == 1) {
            } else {
                cb.initData()
                seletcIndex = 1;
                binding.navA.alpha = 0.7f;
                binding.navB.alpha = 1.0f;
                binding.navC.alpha = 0.7f;
                binding.pgA.visibility = View.GONE
                binding.pgB.visibility = View.VISIBLE
                binding.pgC.visibility = View.GONE
            }
        }
        binding.navC.setOnClickListener {
            if (seletcIndex == 2) {
            } else {
                seletcIndex = 2;
                binding.navA.alpha = 0.7f;
                binding.navB.alpha = 0.7f;
                binding.navC.alpha = 1.0f;
                binding.pgA.visibility = View.GONE
                binding.pgB.visibility = View.GONE
                binding.pgC.visibility = View.VISIBLE
            }
        }

        binding.adLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(WaitressAdHelper.waitUrlStr))
            startActivity(intent)
        }
    }

    var seletcIndex = 0;

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ca.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private var job: Job? = null

    override fun onResume() {
        super.onResume()
        job?.cancel()
        job = lifecycleScope.launch {
            while (WaitressAdHelper.waitUrlStr.isBlank()) {
                delay(2000)
            }
            binding.adLayout.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
    }


}