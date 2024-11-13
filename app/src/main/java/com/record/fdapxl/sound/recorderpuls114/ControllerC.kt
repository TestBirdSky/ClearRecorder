package com.record.fdapxl.sound.recorderpuls114

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.record.fdapxl.sound.recorderpuls114.databinding.ActivityHomeBinding



class ControllerC(val mActivity: AppCompatActivity, val binding: ActivityHomeBinding) {

    fun initRv(){
        binding.privacy.setOnClickListener {
            // TODO:HTTP
            mActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bing.com")))
        }
    }

    init {
        initRv();
    }
}