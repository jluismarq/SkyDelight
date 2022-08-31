package com.example.skydelight.initial

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.CustomLoadingDialog
import com.example.skydelight.databinding.FragmentRecoverPasswordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.*
import java.io.IOException

class RecoverPasswordFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentRecoverPasswordBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        return binding.root

        //return inflater.inflate(R.layout.fragment_recover_password, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clearing errors when produced
        binding.editTxtEmail.doOnTextChanged { _, _, _, _ -> if(binding.FieldEmail.error != null) binding.FieldEmail.error = null }

        binding.btnRecover.setOnClickListener {
            val email = binding.editTxtEmail.text.toString()

            when {
                // Showing alert dialog if email field is empty
                email.isEmpty() -> binding.FieldEmail.error = "Olvidaste colocar tu correo"
                // Showing alert dialog if email field is not an email
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> binding.FieldEmail.error = "Formato de correo no válido"
                // Showing alert dialog if email has more than 50 characters
                email.length > 50 -> binding.FieldEmail.error = "La longitud máxima es de 50 caracteres"
                // Navigating to next fragment if email is correct
                else -> recoverPassword(email)
            }
        }

        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {
            findNavController().navigate(R.id.action_recoverPassword_to_startScreen)
            findNavController().popBackStack(R.id.recover_password_fragment, true) }
    }

    // Function to connect with the api
    private fun recoverPassword(email: String) {
        // Making http request
        val request = Request.Builder()
            .url("https://apiskydelight.herokuapp.com/usuarios/recuperar-contraseña/")
            .post(FormBody.Builder().add("email", email).build())
            .header("KEY-CLIENT", BuildConfig.API_KEY)
            .build()

        // Showing loading dialog
        val customDialog = CustomLoadingDialog(findNavController().context, getString(R.string.loadingDialog_recover))
        customDialog.show()

        // Making HTTP request and getting response
        OkHttpClient().newCall(request).enqueue(object : Callback {
            // Changing to principal fragment if it's successful
            override fun onResponse(call: Call, response: Response){
                // Closing loading dialog
                customDialog.dismiss()

                // Printing api answer
                Log.d("OKHTTP3-CODE", response.code().toString())
                Log.d("OKHTTP3-BODY", response.body()?.string().toString())

                // Code 200 = account verified
                if(response.code() == 200) {
                    activity?.runOnUiThread {
                        val dialog = MaterialAlertDialogBuilder(findNavController().context)
                            .setTitle("¡Recuperación Exitosa!")
                            .setMessage("¡Se te ha enviado un correo para restaurar tu contraseña!")
                            .show()

                        // Closing message and changing to third register fragment
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()
                            findNavController().navigate(R.id.action_recoverPassword_to_startScreen)
                        }, 5000)
                    }
                }
                // Code 401 = account doesn't exist
                else if(response.code() == 404)
                    activity?.runOnUiThread {
                        val dialog = MaterialAlertDialogBuilder(findNavController().context)
                            .setTitle("¡Error! ¡Correo Inválido!")
                            .setMessage("¡El correo que ingresaste no pertenece a ninguna cuenta!")
                            .show()

                        // Closing message
                        Handler(Looper.getMainLooper()).postDelayed({ dialog.dismiss() }, 5000)
                    }
            }

            // Print dialog if it's error
            override fun onFailure(call: Call, e: IOException){
                // Closing loading dialog
                customDialog.dismiss()

                // Printing api answer
                Log.d("OKHTTP3-ERROR", e.toString())

                // Showing message to the user
                activity?.runOnUiThread {
                    val dialog = MaterialAlertDialogBuilder(findNavController().context)
                        .setTitle("¡Ups! ¡Hubo un Problema de Conexión!")
                        .setMessage(e.toString())
                        .show()

                    // Closing message
                    Handler(Looper.getMainLooper()).postDelayed({ dialog.dismiss() }, 5000)
                }
            }
        })
    }
}