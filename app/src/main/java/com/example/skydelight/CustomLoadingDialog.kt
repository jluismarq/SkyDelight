package com.example.skydelight

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CustomLoadingDialog(myContext: Context, text: String) {
    // context and dialog
    private var context = myContext
    private var dialogText = text
    private lateinit var dialog: AlertDialog

    // Show method to create dialog
    fun show() {
        // Creating instance of custom loading dialog xml
        val view = LayoutInflater.from(context).inflate(R.layout.custom_loading_dialog, null)
        view.findViewById<TextView>(R.id.message).text = dialogText

        // Creating and showing dialog
        dialog = MaterialAlertDialogBuilder(context).setView(view).create()
        dialog.show()
    }

    // Dismiss method for close dialog
    fun dismiss() {
        dialog.dismiss()
    }
}