package com.record.fdapxl.sound.recorderpuls114.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.record.fdapxl.sound.recorderpuls114.databinding.DialogInpBinding

object InpDialog {

    fun show(mActivity: AppCompatActivity,title:String,text:String ,callLeft:(String)->Unit,callRight:(String)->Unit){
        val dialog: Dialog= Dialog(mActivity);

        val bin:DialogInpBinding = DialogInpBinding.inflate(mActivity.layoutInflater)
        dialog.setContentView(bin.root)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)
//        dialog.setOnKeyListener { dialog, keyCode, event ->
//            return@setOnKeyListener true
//        }
        bin.title.setText(title)
        bin.input.setText(text)

        bin.leftTv.setOnClickListener {
            val text:String = bin.input.text.toString().trim();
            if (text.isNotEmpty()){
                dialog.dismiss()
                callLeft.invoke(text);
            }
        }
        bin.rightTv.setOnClickListener {
            dialog.dismiss()
            callRight.invoke("")
        }

        val window: Window? = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val attr : WindowManager.LayoutParams? =  window?.attributes
        attr?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = attr

        dialog.show()
    }
}