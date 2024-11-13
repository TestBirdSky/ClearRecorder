package com.record.fdapxl.sound.recorderpuls114.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.record.fdapxl.sound.recorderpuls114.databinding.DialogListBinding

object ListDialog {

    fun show(mActivity: AppCompatActivity,share:()->Unit,delete:()->Unit,rename:()->Unit){
        val dialog: Dialog= Dialog(mActivity);
        val bin:DialogListBinding = DialogListBinding.inflate(mActivity.layoutInflater)
        dialog.setContentView(bin.root)
//        dialog.setCanceledOnTouchOutside(false)
//        dialog.setCancelable(false)
//        dialog.setOnKeyListener { dialog, keyCode, event ->
//            return@setOnKeyListener true
//        }
//
        bin.share.setOnClickListener {
            dialog.dismiss()
            share.invoke()
        }
        bin.delete.setOnClickListener {
            dialog.dismiss()
            delete.invoke();
        }
        bin.rename.setOnClickListener {
            dialog.dismiss()
            rename.invoke();
        }

        val window: Window? = dialog.window
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val attr : WindowManager.LayoutParams? =  window?.attributes
        attr?.width = WindowManager.LayoutParams.MATCH_PARENT
        window?.attributes = attr

        dialog.show()
    }
}