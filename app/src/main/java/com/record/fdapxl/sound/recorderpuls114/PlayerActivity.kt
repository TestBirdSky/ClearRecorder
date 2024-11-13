package com.record.fdapxl.sound.recorderpuls114

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.record.fdapxl.sound.recorderpuls114.bean.Info
import com.record.fdapxl.sound.recorderpuls114.databinding.ActivityPlayerBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlayerActivity : AppCompatActivity() {

    val TAG = "PlayerActivity"
    private lateinit var binding: ActivityPlayerBinding

    var spIndex = 1;
    fun initView(){
        binding.back.setOnClickListener {
            finish()
        }
        binding.sp05.setOnClickListener {
            if (spIndex !=0) {
                spIndex=0;
                setPlaySpeed(0.5f)
                binding.sp05.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A2823"))
                binding.sp10.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp15.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp20.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
            }
        }
        binding.sp10.setOnClickListener {
            if (spIndex !=1) {
                spIndex=1;
                setPlaySpeed(1.0f)
                binding.sp05.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp10.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A2823"))
                binding.sp15.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp20.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
            }
        }
        binding.sp15.setOnClickListener {
            if (spIndex !=2) {
                spIndex=2;
                setPlaySpeed(1.5f)
                binding.sp05.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp10.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp15.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A2823"))
                binding.sp20.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
            }
        }
        binding.sp20.setOnClickListener {
            if (spIndex !=3) {
                spIndex=3;
                setPlaySpeed(2.0f)
                binding.sp05.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp10.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp15.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E5A125"))
                binding.sp20.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#2A2823"))
            }
        }
    }
    companion object {
        var mInfo: Info? = null
        fun start(mActivity: AppCompatActivity,info: Info){
            mInfo = info;
            mActivity.startActivity(Intent(mActivity,PlayerActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
       var info:Info? =  mInfo;
        if (null!=info){
            binding.name.text = info.fileName
            initView()
            fjsalk(info)
        }

        binding.seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val mp:MediaPlayer? = mediaPlayer
                if(null!=mp){
                    mp.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.state.setOnClickListener {
            if (isPlayingPr()){
                binding.stateBg.setBackgroundResource(0)
                binding.state.setBackgroundResource(R.mipmap.m_start)
                pausePr()
            }else{
                binding.stateBg.setBackgroundResource(R.mipmap.m_record_bg)
                binding.state.setBackgroundResource(R.mipmap.m_paun)
                startPr()
            }
        }
    }

    var mediaPlayer:MediaPlayer?=null;
    fun fjsalk( info: Info){
        App.log { "$TAG fjsalk() ${info.filePath}" }
        val mediaPlayer :MediaPlayer= MediaPlayer()
        this.mediaPlayer = mediaPlayer;
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        mediaPlayer.setDataSource(info.filePath)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {mp->
            App.log { "$TAG setOnPreparedListener()" }

            initUI(mp);
            startPr()

//            mp.start();
//
//            val timeString = App.app.convertToCountdown( mp.duration/1000);
//            binding.allTimeTv.text = timeString;
//            binding.seekbar.max = mp.duration
        }
        mediaPlayer.setOnCompletionListener {
            App.log { "$TAG setOnCompletionListener()" }
            binding.stateBg.setBackgroundResource(0)
            binding.state.setBackgroundResource(R.mipmap.m_start)
//            lifecycleScope.launch {
//                delay(500)
                timeStop()
//            }

//            val mp:MediaPlayer = mediaPlayer
//            if(null!=mp){
//                val timeString = App.app.convertToCountdown( mp.duration/1000);
//                binding.allTimeTv.text = timeString;
//            }
        }
        mediaPlayer.setOnErrorListener(object : MediaPlayer.OnErrorListener {
            override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                App.log { "$TAG setOnErrorListener()" }
                return true
            }
        })
    }


    fun initUI(mp: MediaPlayer){
        val timeString = App.app.convertToCountdown( mp.duration/1000);
        binding.allTimeTv.text = timeString;
        binding.seekbar.max = mp.duration

        binding.stateBg.setBackgroundResource(R.mipmap.m_record_bg)
        binding.state.setBackgroundResource(R.mipmap.m_paun)
    }
    fun updateUI (mp: MediaPlayer){

    }

    fun startPr(){
        val mp:MediaPlayer? = mediaPlayer
        if(null!=mp){
           if (mp.isPlaying){ }else{
               mp.start()

               timeStart()
           }
        }
    }

    fun pausePr(){
        val mp:MediaPlayer? = mediaPlayer
        if(null!=mp){
            if (mp.isPlaying){
                mp.pause()

                timeStop()
            }
        }
    }

    fun isPlayingPr(): Boolean {
        val mp:MediaPlayer? = mediaPlayer
        if(null!=mp){
            return mp.isPlaying
        }
        return false
    }

    fun stopPr(){
        val mp:MediaPlayer? = mediaPlayer
        if(null!=mp){
            if (mp.isPlaying){
                mp.stop()
            }
        }
    }

    fun releasePr(){
        val mp:MediaPlayer? = mediaPlayer
        if(null!=mp){
            if (mp.isPlaying){
                mp.stop()
            }
            mp.release();
        }
    }


    var wState = 0; // 0未开始，1播放中，2暂停，


    var timeJob: Job?=null;
    var timeFlag = true;
    fun timeStart(){
        timeFlag = true;
        timeJob =  lifecycleScope.launch(Dispatchers.IO) {
            while (timeFlag){
                delay(1000)
                val mp:MediaPlayer? = mediaPlayer
                if(null!=mp){
                    App.log { "mp.duration=${mp.duration} mp.currentPosition=${mp.currentPosition}" }
                    val timeString = App.app.convertToCountdown( mp.currentPosition/1000);
                    withContext(Dispatchers.Main){
                        binding.seekbar.progress = mp.currentPosition
                        binding.timeCountTv.text = timeString;
//                        if(mp.currentPosition==mp.duration){
//                            binding.state.setBackgroundResource(R.mipmap.m_start)
//                            timeStop()
//                        }
                    }
                }
            }
        }
    }

    fun timeStop(){
        timeFlag = false;
        timeJob?.cancel()
    }

    override fun onDestroy() {
        timeStop();
        releasePr();
        super.onDestroy()
    }


    private fun setPlaySpeed(speed: Float) {
        val mp:MediaPlayer? = mediaPlayer
        if(null!=mp){
            val params = mp.playbackParams
            params.setSpeed(speed)
            mp.playbackParams = params
            if (!timeFlag){

                binding.stateBg.setBackgroundResource(R.mipmap.m_record_bg)
                binding.state.setBackgroundResource(R.mipmap.m_paun)
                timeStart()
            }
        }
    }

}