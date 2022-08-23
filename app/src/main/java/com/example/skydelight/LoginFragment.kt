package com.example.skydelight

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
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
        // TODO("Cancel file creation if user deactivates keep session option")
        binding.btnLogin.setOnClickListener {
            val email = binding.editTxtEmail.text.toString()
            val password = binding.editTxtPassword.text.toString()

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
            // Showing alert dialog if email has more than 50 characters
            else if(email.length > 50){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Correo Inválido!")
                    .setMessage("¡Ups! ¡Parece el correo que ingresaste es demasiado largo!")
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
            // Showing alert dialog if password has less than 8 characters
            else if(password.length < 8){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Contraseña Insegura!")
                    .setMessage("¡Ups! ¡Parece que tu contraseña es demasiado reducida!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Showing alert dialog if password has blank spaces
            else if(password.contains(" ")){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Contraseña Errónea!")
                    .setMessage("¡Ups! ¡Parece que tu contraseña contiene espacios en blanco!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Showing alert dialog if password has more than 50 characters
            else if(password.length > 50){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Contraseña Errónea!")
                    .setMessage("¡Ups! ¡Parece que tu contraseña es demasiado larga!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
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

                    // Creating txt local file
                    val file = File(activity?.getExternalFilesDir(null), "usr_session.txt")
                    val filewr = FileWriter(file)

                    // Clearing file if it has content
                    if(file.exists())
                        filewr.write("")

                    // Saving http response in txt file
                    filewr.use {
                        it.append(json.getString("user")+"\n")
                        it.append(json.getString("name")+"\n")
                        it.append(json.getString("sex")+"\n")
                        it.append(json.getInt("age").toString()+"\n")
                        it.append(json.getString("refresh")+"\n")
                        it.append(json.getString("access"))
                    }

                    // Changing to the principal fragment
                    activity?.runOnUiThread { findNavController().navigate(R.id.action_login_to_principal) }
                }
                // Code 401 = account doesn't exist
                else if(response.code() == 401)
                    activity?.runOnUiThread {
                        val dialog = MaterialAlertDialogBuilder(findNavController().context)
                            .setTitle("¡Error! ¡Credenciales Erróneas!")
                            .setMessage("¡El correo o contraseña son erróneos!")
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