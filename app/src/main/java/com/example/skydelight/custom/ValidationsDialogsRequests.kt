package com.example.skydelight.custom

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.widget.Button
import com.example.skydelight.navbar.NavBarFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import okhttp3.*
import java.io.IOException
import java.util.regex.Pattern

class ValidationsDialogsRequests {
    fun dialog(title: String, message: String, context: Context){
        val dialog = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .show()

        // Closing message
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
        }, 5000)
    }

    fun dialogOnUIThread(title: String, message: String, context: Context, activity: Activity, button1: Button?,
                         button2: Button?, button3: Button?, button4: Button?, parentFragment: NavBarFragment?,
                              function: () -> (Unit)){
        activity.runOnUiThread {
            val dialog = MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .show()

            // Closing message
            Handler(Looper.getMainLooper()).postDelayed({
                // Activating buttons
                button1?.isClickable = true
                button2?.isClickable = true
                button3?.isClickable = true
                button4?.isClickable = true
                parentFragment?.changeNavBarButtonsClickable(true)
                dialog.dismiss()
                function()
            }, 5000)
        }
    }

    fun validateEmail(email: String, fieldEmail: TextInputLayout) : Boolean{
        when {
            // Showing alert dialog if email field is empty
            email.isEmpty() -> fieldEmail.error = "Olvidaste colocar tu correo"
            // Showing alert dialog if email field is not an email
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> fieldEmail.error = "Formato de correo no válido"
            // Showing alert dialog if email has more than 50 characters
            email.length > 50 -> fieldEmail.error = "La longitud máxima es de 50 caracteres"
            // Connection to the api and sending the password to the email
            else -> return true
        }
        return false
    }

    fun validatePassword(password: String, fieldPassword: TextInputLayout) : Boolean{
        when {
            // Showing alert dialog if password field is empty
            password.isEmpty() -> fieldPassword.error = "Olvidaste colocar tu contraseña"
            // Showing alert dialog if password has less than 8 characters
            password.length < 8 -> fieldPassword.error = "La longitud mínima es de 8 caracteres"
            // Showing alert dialog if password has blank spaces
            password.contains(" ") -> fieldPassword.error = "No se permiten espacios en blanco"
            // Showing alert dialog if password has more than 50 characters
            password.length > 50 -> fieldPassword.error = "La longitud máxima es de 50 caracteres"
            // Connection to the api and sending the password to the email
            else -> return true
        }
        return false
    }

    fun validateConfirmedPassword(password: String, confirmedPassword: String, fieldPassword: TextInputLayout) : Boolean{
        when {
            // Showing alert dialog if confirm password field is empty
            confirmedPassword.isEmpty() -> fieldPassword.error = "Olvidaste confirmar tu contraseña"
            // Showing alert dialog if password and confirmedPassword don't match
            password != confirmedPassword -> fieldPassword.error = "Esta contraseña es distinta a la primera"
            else -> return true
        }
        return false
    }

    fun validateName(name: String, fieldName: TextInputLayout) : Boolean{
        // Showing alert dialog if name field is empty
        when {
            name.isEmpty() -> fieldName.error = "Olvidaste colocar tu nombre"
            // Showing alert dialog if name contains numbers or special characters
            !Pattern.matches("[a-zA-ZñÑ áéíóúÁÉÍÓÚ]+", name) -> fieldName.error = "No se permiten ese tipo de caracteres"
            // Showing alert dialog if name has more than 50 characters
            name.length > 50 -> fieldName.error = "La longitud máxima es de 50 caracteres"
            else -> return true
        }
        return false
    }

    fun validateSex(sexId: Int, context: Context, title: String, message: String): Boolean{
        if(sexId != -1)
            return true
        else
            dialog(title, message, context)
        return false
    }

    fun httpPetition(request: Request, context: Context, activity: Activity, button1: Button?, button2: Button?,
                     button3: Button?, button4: Button?, dialogText: String?, codeError: Int?, errorTitle: String?,
                     errorMessage: String?, parentFragment: NavBarFragment?, function: (String) -> (Unit)){
        val customDialog = CustomLoadingDialog(context, dialogText ?: "")

        // Showing loading dialog
        if (dialogText != null) customDialog.show()

        // Making HTTP request and getting response
        OkHttpClient().newCall(request).enqueue(object : Callback {
            // Changing to principal fragment if it's successful
            override fun onResponse(call: Call, response: Response){
                // Closing loading dialog
                if (dialogText != null) customDialog.dismiss()

                // Printing api answer
                val responseString = response.body()?.string().toString()
                Log.d("OKHTTP3-CODE", response.code().toString())
                Log.d("OKHTTP3-BODY", responseString)

                // Code 200 = account verified
                if(response.code() in 200..202) {
                    function(responseString)
                }
                // Custom error code
                else if(response.code() == (codeError ?: 400..405))
                    ValidationsDialogsRequests().dialogOnUIThread(errorTitle ?: "¡Ups! ¡Hubo un Problema de Conexión!",
                        errorMessage ?:"¡Parece que Estamos Teniendo Algunas Dificultades!", context, activity,
                        button1, button2, button3, button4, parentFragment){}
                // Connection or server errors
                else if(response.code() in 400..405 || response.code() == 500)
                    ValidationsDialogsRequests().dialogOnUIThread("¡Ups! ¡Hubo un Problema de Conexión!",
                        "¡Parece que Estamos Teniendo Algunas Dificultades!",
                        context, activity, button1, button2, button3, button4, parentFragment){}
            }

            // Print dialog if it's error
            override fun onFailure(call: Call, e: IOException){
                // Closing loading dialog
                if (dialogText != null) customDialog.dismiss()

                // Printing api answer
                Log.d("OKHTTP3-ERROR", e.toString())

                // Showing message to the user
                ValidationsDialogsRequests().dialogOnUIThread("¡Ups! ¡Hubo un Problema de Conexión!",
                    "¡Parece que Estamos Teniendo Algunas Dificultades!",
                    context, activity, button1, button2, button3, button4, parentFragment) {}
            }
        })
    }
}