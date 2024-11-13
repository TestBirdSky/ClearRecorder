package com.record.fdapxl.sound.recorderpuls114

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.record.fdapxl.sound.recorderpuls114.bean.Info
import com.tencent.mmkv.MMKV
import java.io.File
import java.text.DecimalFormat


class App: Application() {

    companion object {
        lateinit var  app:App

       fun log(call:()->String){
            Log.i("FFF",call.invoke())
        }
    }

    lateinit var mmkv: MMKV
    override fun onCreate() {
        super.onCreate()
        app = this;
        MMKV.initialize(this)
        mmkv = MMKV.defaultMMKV()
    }

    fun putSet(key:String,value:Set<String>){
        mmkv.encode(key,value)
    }
    fun getSet(key:String): MutableSet<String>{
        return  mmkv.decodeStringSet(key)?: mutableSetOf()
    }



    var targetPath:String = "";

    fun getPath(): String {
       var path =  targetPath;
        if (path.isEmpty()){
            // need  Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
//            path = Environment.getExternalStorageDirectory().path+File.separator+ Environment.DIRECTORY_MUSIC+File.separator+"My_Recordings"
            path = "${this.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.path}${File.separator}My_Recordings"
        }
        return path;
    }

    val KEY_RECORDINGS_LIST = "KEY_RECORDINGS_LIST"
    fun addRecord(text:String){
        App.log { "addRecord() text=$text" }
        val set = getSet(KEY_RECORDINGS_LIST);
        set.add(text)
        putSet(KEY_RECORDINGS_LIST,set)
    }

    fun getRecordList(): List<Info>? {
        val set:MutableSet<String> = getSet(KEY_RECORDINGS_LIST);
       val list:List<Info> = set.map {
           App.log { "getRecordList() $it" }
            val arr = it.split("###")
            Info(arr[0].toLong(), arr[1])
        }
        list.sortedBy { it.time }
        return list;
    }

    fun rmRecord(info: Info){
//        App.log { "addRecord() text=$text" }
        val set = getSet(KEY_RECORDINGS_LIST);

       val newSet =  set.filterNot {
            it.startsWith("${info.time}")
        }.toSet()

        putSet(KEY_RECORDINGS_LIST,newSet)
    }

    fun updateRecord(info: Info,text: String){
        val set = getSet(KEY_RECORDINGS_LIST);

        for ((index, vals) in set.withIndex()) {
            if (vals.startsWith("${info.time}")){
                set.remove(vals)
                set.add(text)
                break
            }
        }

        putSet(KEY_RECORDINGS_LIST,set)
    }

//    fun saveAudio(){
//        val fileName = "AUDIO_"+System.currentTimeMillis()
//        val values = ContentValues()
//        values.put(MediaStore.Audio.Media.DISPLAY_NAME,fileName)
//        values.put(MediaStore.Audio.Media.MIME_TYPE,"audio/amr")
//        values.put(MediaStore.Audio.Media.DATE_MODIFIED,System.currentTimeMillis())
//        values.put(MediaStore.Audio.Media.DATE_ADDED,System.currentTimeMillis())
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
//        }else{
//            val path = getPath()+ File.separator+fileName
//            values.put(MediaStore.Images.Media.DATA,path)
//        }
//
//        val uri: Uri?= contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//        if (null!=uri){
//            val out : OutputStream? = contentResolver.openOutputStream(uri)
//            if (null!=out){
//
//            }
//        }
//    }

    fun convertToCountdown(value: Int): String {
        val hours = value / 3600
        val minutes = value % 3600/60
        val seconds = value % 60
        val format: DecimalFormat = DecimalFormat("00")
        if (hours>0){
           return format.format(hours) + ":" + format.format(minutes) + ":" + format.format(seconds)
        }else{
            return format.format(minutes) + ":" + format.format(seconds)
        }
    }

    var permissionsRequestTime:Long = 0L
    fun checkPermissionAndRequest(mActivity: AppCompatActivity, call:()->Unit){
        if (ContextCompat.checkSelfPermission(mActivity,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            call.invoke()
        }else{
            permissionsRequestTime = System.currentTimeMillis()
            ActivityCompat.requestPermissions(mActivity, arrayOf(android.Manifest.permission.RECORD_AUDIO),888)
        }
    }



//    var needPermissions: Array<String> = arrayOf(
//        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//        Manifest.permission.RECORD_AUDIO
//    )
//    fun checkPermissionAndRequest2(mActivity: AppCompatActivity, call:()->Unit){
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
//            val deniedPermissions = mutableListOf<String>()
//            for ((index, vals) in needPermissions.withIndex()) {
//                if (ActivityCompat.checkSelfPermission(this,vals)!= PackageManager.PERMISSION_GRANTED){
//                    log { "checkPermissionAndRequest() vals=${vals}" }
//                    deniedPermissions.add(vals)
//                }
//            }
//            if (deniedPermissions.isEmpty()){
//                call.invoke()
//            }else{
//                permissionsRequestTime = System.currentTimeMillis()
//                val ss = deniedPermissions.toTypedArray()
////                ActivityCompat.requestPermissions(mActivity, arrayOf(android.Manifest.permission.RECORD_AUDIO),888)
//                ActivityCompat.requestPermissions(mActivity, ss,888)
//            }
//        }else{
//            if (ContextCompat.checkSelfPermission(mActivity,android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
//                call.invoke()
//            }else{
//                permissionsRequestTime = System.currentTimeMillis()
//                ActivityCompat.requestPermissions(mActivity, arrayOf(android.Manifest.permission.RECORD_AUDIO),888)
//            }
//        }
//    }






    fun shareFile(mActivity: AppCompatActivity,info: Info){

        val fileUri: Uri = FileProvider.getUriForFile(this, "com.record.fdapxl.sound.recorderpuls114_share",      info.file)

        val intent:Intent  = Intent()
        intent.action = Intent.ACTION_SEND
        intent.setType("audio/*")

        intent.putExtra(Intent.EXTRA_STREAM, fileUri);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        mActivity.startActivity(Intent.createChooser(intent,"Share Recording"))
    }

    fun toast(s: String) {
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show()
    }

}