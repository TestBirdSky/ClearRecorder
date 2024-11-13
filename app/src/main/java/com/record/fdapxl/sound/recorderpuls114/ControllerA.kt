package com.record.fdapxl.sound.recorderpuls114

import android.content.Intent
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.record.fdapxl.sound.recorderpuls114.databinding.ActivityHomeBinding
import com.record.fdapxl.sound.recorderpuls114.dialog.InpDialog
import com.record.fdapxl.sound.recorderpuls114.dialog.SpDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ControllerA(val mActivity: AppCompatActivity,val binding: ActivityHomeBinding) {


    init {

        binding.state.setOnClickListener {
            App.app.checkPermissionAndRequest(mActivity){
                if (wState ==0){
                    binding.saveTv.visibility = View.VISIBLE
                    wState = 1
                    binding.state.setBackgroundResource(R.mipmap.m_record)
                    binding.stateBg.setBackgroundResource(R.mipmap.m_record_bg)
                    timeStart()
                    startRecord();
                }else   if (wState ==1){
                    wState = 2
                    binding.state.setBackgroundResource(R.mipmap.m_start)
                    binding.stateBg.setBackgroundResource(0);
                    timeStop()
                    pauseRecord()
                }else   if (wState ==2){
                    wState = 1
                    binding.state.setBackgroundResource(R.mipmap.m_record)
                    binding.stateBg.setBackgroundResource(R.mipmap.m_record_bg)
                    timeStart()
                    resumeRecord()
                }

            }
        }
        binding.saveTv.setOnClickListener {
            binding.state.setBackgroundResource(R.mipmap.m_start)
            binding.stateBg.setBackgroundResource(0);
            wState = 0
            timeCount = 0;
            binding.timeCountTv.text = "00:00"
            timeStop();
            stopRecord();
            App.log { "ControllerA" }
            val tmpFile:File? = tmpSoundFile;
            if (tmpFile!=null){

               var sss1 =  tmpFile.name.substringAfter(".");
               var sss2 =  tmpFile.name.substringBefore(".");
               var sss3 =  tmpFile.name.substringAfterLast(".");
               var sss4 =  tmpFile.name.substringBeforeLast(".");

                App.log { "ControllerA $sss1 $sss2 $sss3 $sss4" }

                InpDialog.show(mActivity,"Save recording",sss2,{ text ->
                    val oleFile: File = tmpFile
                    val newFile: File = File(tmpFile.parent, "$text.aac")
                    //执行重命名
                    oleFile.renameTo(newFile)

                    App.app.toast("Recording completed");
                    App.app.addRecord("${System.currentTimeMillis()}###${newFile}")
                },{txt->
                    tmpFile.delete()
                })
            }
            binding.saveTv.visibility = View.GONE
        }
    }

    var tmpSoundFile :File? = null


    private var mediaRecorder: MediaRecorder? = null
    //开始录制
    private fun startRecord() {
        var mr: MediaRecorder? = mediaRecorder;
        if (mr==null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                mr = MediaRecorder(mActivity)
            }else{
                mr = MediaRecorder();
            }
        }
        mediaRecorder = mr;

        val dir = File(App.app.getPath())
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val time = SimpleDateFormat("HH_mm_ss_MM_dd_yyyy", Locale.getDefault()).format(Date(System.currentTimeMillis()))

        val soundFile = File(dir, "Recording_$time.aac")
        if (!soundFile.exists()) {
            try {
                soundFile.createNewFile()
                tmpSoundFile = soundFile;
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        mr.setAudioSource(MediaRecorder.AudioSource.MIC) //音频输入源
        mr.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS) //设置输出格式
        mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC) //设置编码格式
        mr.setOutputFile(soundFile.absolutePath)
//        mr.setOnInfoListener(object : MediaRecorder.OnInfoListener {
//            override fun onInfo(mr: MediaRecorder?, what: Int, extra: Int) {
//
//            }
//        })



        try {
            mr.prepare()
            mr.start() //开始录制
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun pauseRecord() {
        var mr = mediaRecorder;
        if (mr != null) {
            mr.pause()
        }
    }


    private fun resumeRecord() {
        var mr = mediaRecorder;
        if (mr != null) {
            mr.resume()
        }
    }

    //停止录制，资源释放
    private fun stopRecord() {
        var mr = mediaRecorder;
        if (mr != null) {
            mr.stop()
            mr.release()
            mr = null
        }
        mediaRecorder = null
    }

    var wState = 0; // 0未开始，1录制中，2暂停，

    var timeCount:Int = 0;
    var timeJob: Job?=null;
    var timeFlag = true;
    fun timeStart(){
        timeFlag = true;
        timeJob =  mActivity.lifecycleScope.launch(Dispatchers.IO) {
            while (timeFlag){
                delay(1000)
                timeCount += 1
                val timeString = App.app.convertToCountdown(timeCount);
                withContext(Dispatchers.Main){
                    binding.timeCountTv.text = timeString;
                }
            }
        }
    }

    fun timeStop(){
        timeFlag = false;
        timeJob?.cancel()
    }

     fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
         if (500>System.currentTimeMillis() - App.app.permissionsRequestTime){
             SpDialog.show(mActivity,"Need to modify permission settings",{
                 val uri = Uri.parse("package:${App.app.packageName}")
                 val intent: Intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                 intent.setData(uri)
                 mActivity.startActivityForResult(intent, 1000)
             })
         }
    }
}