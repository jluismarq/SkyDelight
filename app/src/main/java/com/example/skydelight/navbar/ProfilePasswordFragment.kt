package com.example.skydelight.navbar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.custom.CustomLoadingDialog
import com.example.skydelight.custom.User
import com.example.skydelight.databinding.FragmentNavbarProfilePasswordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class ProfilePasswordFragment : Fragment() {

    // Binding variable to use elements in the xml layout
    private lateinit var binding: FragmentNavbarProfilePasswordBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentNavbarProfilePasswordBinding.inflate(inflater, container, false)
        return binding.root

        // return inflater.inflate(R.layout.fragment_navbar_profile_password, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clearing errors when produced
        binding.editTxtPassword.doOnTextChanged { _, _, _, _ -> if(binding.FieldPassword.error != null) binding.FieldPassword.error = null }
        binding.editTxtConfirmPassword.doOnTextChanged { _, _, _, _ ->
            if(binding.FieldConfirmPassword.error != null) binding.FieldConfirmPassword.error = null }

        // Changing to updating password fragment
        binding.btnCancel.setOnClickListener {
            // Fragment enters from right
            (parentFragment as NavBarFragment).updateNavBarHost(ProfileFragment(), R.id.nav_profile, false)
        }

        binding.btnUpdate.setOnClickListener{
            val password = binding.editTxtPassword.text.toString()
            val confirmedPassword = binding.editTxtConfirmPassword.text.toString()

            // Showing alert dialog if password field is empty
            when {
                password.isEmpty() -> binding.FieldPassword.error = "Olvidaste colocar tu contraseña"
                // Showing alert dialog if password has less than 8 characters
                password.length < 8 -> binding.FieldPassword.error = "La longitud mínima es de 8 caracteres"
                // Showing alert dialog if password has blank spaces
                password.contains(" ") -> binding.FieldPassword.error = "No se permiten espacios en blanco"
                // Showing alert dialog if password has more than 50 characters
                password.length > 50 -> binding.FieldPassword.error = "La longitud máxima es de 50 caracteres"
                // Showing alert dialog if confirm password field is empty
                confirmedPassword.isEmpty() -> binding.FieldConfirmPassword.error = "Olvidaste confirmar tu contraseña"
                // Showing alert dialog if password and confirmedPassword don't match
                password != confirmedPassword -> binding.FieldConfirmPassword.error = "Esta contraseña es distinta a la primera"
                // Connection to the api and creation of the new user
                else -> updatePassword(password)
            }
        }
    }

    // Function to connect with the api
    private fun updatePassword(password: String){
        // Launching room database connection
        MainScope().launch {
            // Creating connection to database
            val userDao = Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                .fallbackToDestructiveMigration().build().userDao()
            val user = userDao.getUser()[0]

            // Arguments to Post Request
            val formBody: RequestBody = FormBody.Builder()
                .add("email", user.email)
                .add("password", password)
                .build()

            // TODO("Verify if token is valid, if not, change it")
            // Making http request
            val request = Request.Builder()
                .url("https://apiskydelight.herokuapp.com/usuarios/cambiar-contrasena/")
                .put(formBody)
                .addHeader("Authorization", "Bearer " + user.token)
                .addHeader("KEY-CLIENT", BuildConfig.API_KEY)
                .build()

            // Showing loading dialog
            val customDialog = CustomLoadingDialog(findNavController().context, getString(R.string.loadingDialog_updating))
            customDialog.show()

            // Making HTTP request and getting response
            OkHttpClient().newCall(request).enqueue(object : Callback {
                // Changing to principal fragment if it's successful
                override fun onResponse(call: Call, response: Response){
                    // Closing loading dialog
                    customDialog.dismiss()

                    // Printing api answer
                    val responseString = response.body()?.string().toString()
                    Log.d("OKHTTP3-CODE", response.code().toString())
                    Log.d("OKHTTP3-BODY", responseString)

                    // Showing succesful dialog
                    activity?.runOnUiThread {
                        val dialog = MaterialAlertDialogBuilder(findNavController().context)
                            .setTitle("¡Actualización Exitosa!")
                            .setMessage("¡Tu Contraseña se Actualizó Correctamente!")
                            .setCancelable(false)
                            .show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()

                            // Fragment enters from right
                            (parentFragment as NavBarFragment).updateNavBarHost(
                                ProfileFragment(),
                                R.id.nav_profile,
                                false
                            )
                        }, 5000)
                    }
                }

                // TODO("Verify all classes failures and add a dialog")
                // Print dialog if it's error
                override fun onFailure(call: Call, e: IOException){
                    // Closing loading dialog
                    customDialog.dismiss()

                    // Printing api answer
                    Log.d("OKHTTP3-ERROR", e.toString())
                }
            })
        }
    }
}