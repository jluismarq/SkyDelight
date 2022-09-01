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
import androidx.room.Room
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.custom.CustomLoadingDialog
import com.example.skydelight.custom.User
import com.example.skydelight.databinding.FragmentLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class LoginFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentLoginBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

        //return inflater.inflate(R.layout.fragment_login, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clearing errors when produced
        binding.editTxtEmail.doOnTextChanged { _, _, _, _ -> if(binding.FieldEmail.error != null) binding.FieldEmail.error = null }
        binding.editTxtPassword.doOnTextChanged { _, _, _, _ -> if(binding.FieldPassword.error != null) binding.FieldPassword.error = null }

        binding.btnLogin.setOnClickListener {
            val email = binding.editTxtEmail.text.toString()
            val password = binding.editTxtPassword.text.toString()

            // Showing alert dialog if email field is empty
            if(email.isEmpty())
                binding.FieldEmail.error = "Olvidaste colocar tu correo"
            // Showing alert dialog if email field is not an email
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                binding.FieldEmail.error = "Formato de correo no válido"
            // Showing alert dialog if email has more than 50 characters
            else if(email.length > 50)
                binding.FieldEmail.error = "La longitud máxima es de 50 caracteres"
            // Showing alert dialog if password field is empty
            else if(password.isEmpty())
                binding.FieldPassword.error = "Olvidaste colocar tu contraseña"
            // Showing alert dialog if password has less than 8 characters
            else if(password.length < 8)
                binding.FieldPassword.error = "La longitud mínima es de 8 caracteres"
            // Showing alert dialog if password has blank spaces
            else if(password.contains(" "))
                binding.FieldPassword.error = "No se permiten espacios en blanco"
            // Showing alert dialog if password has more than 50 characters
            else if(password.length > 50)
                binding.FieldPassword.error = "La longitud máxima es de 50 caracteres"
            // Connection to the api and sending the password to the email
            else
                login(email, password)
        }

        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_startScreen)
            findNavController().popBackStack(R.id.login_fragment, true) }
    }

    // Function to connect with the api
    private fun login(email: String, password: String) {
        // Arguments to Post Request
        val formBody: RequestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .build()

        // Making http request
        val request = Request.Builder()
            .url("https://apiskydelight.herokuapp.com/usuarios/token/obtener/")
            .post(formBody)
            .header("KEY-CLIENT", BuildConfig.API_KEY)
            .build()

        // Showing loading dialog
        val customDialog = CustomLoadingDialog(findNavController().context, getString(R.string.loadingDialog_login))
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

                // Code 200 = account verified
                if(response.code() == 200) {
                    // Changing http body to json
                    val json = JSONObject(responseString)

                    // Launching room database connection
                    MainScope().launch {
                        // Creating connection to database
                        val userDao =
                            Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                                .fallbackToDestructiveMigration().build().userDao()

                        // If user exists, we have to delete it
                        val user = userDao.getUser()
                        if(user.isNotEmpty())
                            userDao.deleteUser(user[0])

                        // Adding the new user to the database
                        userDao.insertUser(User(json.getString("user"), json.getString("name"),
                            json.getString("sex"), json.getInt("age"), json.getString("access"),
                            json.getString("refresh"), binding.rememberSession.isChecked))

                        // Changing to the principal fragment
                        activity?.runOnUiThread { findNavController().navigate(R.id.action_login_to_navBar) }
                    }
                }
                // Code 401 = account doesn't exist
                else if(response.code() == 401)
                    activity?.runOnUiThread {
                        val dialog = MaterialAlertDialogBuilder(findNavController().context)
                            .setTitle("¡Error! ¡Acceso Prohibido!")
                            .setMessage("¡No Existe la Cuenta o el Correo y Contraseña son Erróneos!")
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