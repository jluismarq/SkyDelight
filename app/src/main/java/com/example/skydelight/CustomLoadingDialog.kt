package com.example.skydelight

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomLoadingDialog(myContext: Context) {
    // context and dialog
    private var context = myContext
    private lateinit var dialog: AlertDialog

    // Creating and showing dialog
    fun show() {
        dialog = MaterialAlertDialogBuilder(context).setView(R.layout.custom_loading_dialog).create()
        dialog.show()
    }

    // dismiss method for close dialog
    fun dismiss() {
        dialog.dismiss()
    }
}