package com.example.chatting.loadingdialog

import android.app.Activity
import android.app.AlertDialog
import android.graphics.drawable.ColorDrawable
import com.example.chatting.R

class LoadingDialog(private val activity : Activity)  {
    private lateinit var isDialog : AlertDialog

    fun startDialog(){
        val inflate = activity.layoutInflater
        val dialogView = inflate.inflate(R.layout.loading_item, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        isDialog.show()
    }

    fun isDismiss(){
        isDialog.dismiss()
    }
}