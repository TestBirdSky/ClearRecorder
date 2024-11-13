package com.record.fdapxl.sound.recorderpuls114.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.record.fdapxl.sound.recorderpuls114.databinding.DialogSpBinding

object SpDialog {

    fun show(mActivity: AppCompatActivity,text:String,callLeft:()->Unit,){
        val dialog: Dialog= Dialog(mActivity);

        val bin:DialogSpBinding = DialogSpBinding.inflate(mActivity.layoutInflater)
        dialog.setContentView(bin.root)

        bin.titleTv.text = text;
        bin.leftTv.setOnClickListener {
            dialog.dismiss()
            callLeft.invoke();
        }
        bin.rightTv.setOnClickListener {
            dialog.dismiss()
        }


        val window: Window? = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val attr : WindowManager.LayoutParams? =  window?.attributes
        attr?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = attr

        dialog.show()
    }
}