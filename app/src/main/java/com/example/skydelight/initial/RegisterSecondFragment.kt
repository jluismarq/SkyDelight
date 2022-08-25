package com.example.skydelight.initial

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.custom.CustomLoadingDialog
import com.example.skydelight.custom.User
import com.example.skydelight.databinding.FragmentRegisterSecondBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.FileWriter
import java.io.IOException

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
            // Showing alert dialog if password and confirmedPassword don't match
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
            else
                createUser(email, password, name.toString(), sex.toString(), age.toString())
        }

        // Returning to the register first fragment
        binding.btnReturn.setOnClickListener {
            // Setting parameters for the next fragment
            val bundle = bundleOf(NAME_PARAM to name, SEX_PARAM to sex, AGE_PARAM to age)

            // Starting previous fragment
            findNavController().navigate(R.id.action_registerSecond_to_registerFirst, bundle)
            findNavController().popBackStack(R.id.register_second_fragment, true)
        }
    }

    // Function to connect with the api
    private fun createUser(email: String, password: String, name: String, sex: String, age: String) {
        /* Alternative to get api key
        val info: ApplicationInfo = findNavController().context.packageManager
            .getApplicationInfo(findNavController().context.packageName, PackageManager.GET_META_DATA)
        info.metaData.getString("com.google.android.geo.API_KEY").toString()*/

        // Arguments to Post Request
        var formBody: RequestBody = FormBody.Builder()
            .add("email", email)
            .add("password", password)
            .add("name", name)
            .add("sex", sex)
            .add("edad", age)
            .build()

        // Making http request
        var request = Request.Builder()
            .url("https://apiskydelight.herokuapp.com/usuarios/crearusuario/")
            .post(formBody)
            .header("KEY-CLIENT", BuildConfig.API_KEY)
            .build()

        // Showing loading dialog
        val customDialog = CustomLoadingDialog(findNavController().context, getString(R.string.loadingDialog_creating))
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

                // Code 201 = account created
                if(response.code() == 201) {
                    // Arguments to Post Request
                    formBody = FormBody.Builder()
                        .add("email", email)
                        .add("password", password)
                        .build()

                    // Making http request
                    request = Request.Builder()
                        .url("https://apiskydelight.herokuapp.com/usuarios/token/obtener/")
                        .post(formBody)
                        .header("KEY-CLIENT", BuildConfig.API_KEY)
                        .build()

                    // Making HTTP request and getting response
                    OkHttpClient().newCall(request).enqueue(object : Callback {
                        // Changing to principal fragment if it's successful
                        override fun onResponse(call: Call, response: Response){
                            // Changing http body to json
                            val json = JSONObject(response.body()?.string().toString())

                            // Launching room database connection
                            MainScope().launch {
                                // Creating connection to database
                                val userDao =
                                    Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                                        .build().userDao()

                                // If user exists, we have to delete it
                                val user = userDao.getUser()
                                if(user.isNotEmpty())
                                    userDao.deleteUser(user[0])

                                // Adding the new user to the database
                                userDao.insertUser(User(json.getString("user"), json.getString("name"),
                                    json.getString("sex"), json.getInt("age"), json.getString("refresh"),
                                    json.getString("access")))

                                // Success dialog for the user
                                activity?.runOnUiThread {
                                    val dialog = MaterialAlertDialogBuilder(findNavController().context)
                                        .setTitle("¡Registro Exitoso!")
                                        .setMessage("¡Tu cuenta ha sido creada correctamente!")
                                        .show()

                                    // Closing message and changing to third register fragment
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        dialog.dismiss()
                                        findNavController().navigate(R.id.action_registerSecond_to_registerThird)
                                    }, 5000)
                                }
                            }
                        }

                        // Print dialog if it's error
                        override fun onFailure(call: Call, e: IOException){
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
                // Code 400 = account already exists
                else if(response.code() == 400)
                    activity?.runOnUiThread {
                        val dialog = MaterialAlertDialogBuilder(findNavController().context)
                            .setTitle("¡Error! ¡Cuenta Existente!")
                            .setMessage("¡Ya existe una cuenta con ese correo!")
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