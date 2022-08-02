package com.example.skydelight

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentRegisterSecondBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.*
import android.util.Log
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager

private const val NAME_PARAM = "name"
private const val AGE_PARAM = "age"
private const val SEX_PARAM = "sex"

class RegisterSecondFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding: FragmentRegisterSecondBinding

    // Variables to receive data from other fragments
    private var name: String? = null
    private var age: String? = null
    private var sex: String? = null

    // Getting data from other fragments
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString(NAME_PARAM)
            age = it.getString(AGE_PARAM)
            sex = it.getString(SEX_PARAM)
        }
    }

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterSecondBinding.inflate(inflater, container, false)
        return binding.root

        // return inflater.inflate(R.layout.fragment_register_second, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreateAccount.setOnClickListener{
            val email = binding.editTxtEmail.text.toString()
            val password = binding.editTxtPassword.text.toString()
            val confirmedPassword = binding.editTxtConfirmPassword.text.toString()

            // Showing alert dialog if email field is empty
            if(email.isEmpty()){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Campo Vacío!")
                    .setMessage("¡Ups! ¡Parece que olvidaste colocar tu correo!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Showing alert dialog if email field is not an email
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Correo Inválido!")
                    .setMessage("¡Ups! ¡Parece que no ingresaste un correo electrónico!")
                    //.setNegativeButton("¡Entendido!"){ dialog, which -> dialog.dismiss() }
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Showing alert dialog if password field is empty
            else if(password.isEmpty()){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Campo Vacío!")
                    .setMessage("¡Ups! ¡Parece que olvidaste colocar tu contraseña!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Showing alert dialog if confirm password field is empty
            else if(confirmedPassword.isEmpty()){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Campo Vacío!")
                    .setMessage("¡Ups! ¡Parece que olvidaste confirmar tu contraseña!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Showing alert dialog if confirm password field is empty
            else if(password != confirmedPassword){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Contraseñas Distintas!")
                    .setMessage("¡Ups! ¡Parece que colocaste mal una de tus contraseñas!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Connection to the api and creation of the new user
            else { createUser(email, password, name.toString(), sex.toString(), age.toString()) }
        }

        // TODO("Send the data received to the previous fragment")
        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {findNavController().navigate(R.id.action_registerSecond_to_registerFirst)}
    }

    // Function to connect with the api
    @Suppress("DEPRECATION")
    private fun createUser(email: String, password: String, name: String, sex: String, age: String) {
        // TODO("Change getApplicationInfo because is deprecated")
        val info: ApplicationInfo = findNavController().context.packageManager.getApplicationInfo(findNavController().context.packageName, PackageManager.GET_META_DATA)

        // Arguments to Post Request
        val formBody: RequestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .add("name", name)
            .add("sex", sex)
            .add("edad", age)
            .build()

        // Making http request
        val request = Request.Builder()
            .url("https://apiskydelight.herokuapp.com/usuarios/crearusuario/")
            .post(formBody)
            .header("KEY-CLIENT", info.metaData.getString("com.google.android.geo.API_KEY").toString())
            .build()

        // Creating instance for http client and getting response
        val response : Response = OkHttpClient().newCall(request).execute()

        // Changing to principal fragment if it's successful
        if (response.isSuccessful){
            // TODO("Create Third Register Screen with Explanation")
            Log.d("OKHTTP3-CODE", response.code().toString())
            Log.d("OKHTTP3-BODY", response.body()!!.string())
        } else {
            // TODO("Print dialog with explanation to the user")
            Log.d("OKHTTP3-ERROR", response.toString())
        }
    }
}