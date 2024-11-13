package com.record.fdapxl.sound.recorderpuls114.bean

import java.io.File

class Info public constructor(val time:Long,val filePath:String){

    val file:File
    val fileName:String
    init {
        file = File(filePath)
        fileName = file.name
    }
}