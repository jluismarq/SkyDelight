package com.example.skydelight.navbar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.custom.CustomLoadingDialog
import com.example.skydelight.custom.User
import com.example.skydelight.databinding.FragmentNavbarProfileDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException
import java.util.regex.Pattern

class ProfileDataFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentNavbarProfileDataBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentNavbarProfileDataBinding.inflate(inflater, container, false)
        return binding.root

        //return inflater.inflate(R.layout.fragment_navbar_profile_data, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Changing Default Values of Age Number Picker
        binding.numberPickerAge.minValue = 18
        binding.numberPickerAge.maxValue = 25

        // Showing user data in profile screen fragment
        showUserData()

        // Clearing errors when produced
        binding.editTxtName.doOnTextChanged { _, _, _, _ -> if(binding.FieldName.error != null) binding.FieldName.error = null }

        // Changing to updating password fragment
        binding.btnCancel.setOnClickListener {
            // Fragment enters from right
            (parentFragment as NavBarFragment).updateNavBarHost(ProfileFragment(), R.id.nav_profile, false)
        }

        binding.btnUpdate.setOnClickListener {
            // Getting sex option selected and email
            val sexId = binding.radioGroupSex.checkedRadioButtonId

            // Getting user answers
            val name = binding.editTxtName.text.toString()
            val age = binding.numberPickerAge.value
            val sex = binding.radioGroupSex.findViewById<RadioButton>(sexId)?.text.toString()

            // Showing alert dialog if name field is empty
            if(name.isEmpty())
                binding.FieldName.error = "Olvidaste colocar tu nombre"
            // Showing alert dialog if name contains numbers or special characters
            else if(!Pattern.matches("[a-zA-ZñÑ áéíóúÁÉÍÓÚ]+", name))
                binding.FieldName.error = "No se permiten ese tipo de caracteres"
            // Showing alert dialog if name has more than 50 characters
            else if(name.length > 50)
                binding.FieldName.error = "La longitud máxima es de 50 caracteres"
            // Showing alert dialog if user didn't choose a sex
            else if(sexId == -1){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Elemento Faltante!")
                    .setMessage("¡Ups! ¡Parece que olvidaste elegir tu sexo!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
            // Connection to the api and sending the password to the email
            else updateData(name, sex, age)
        }
    }

    // Function to connect with the api
    private fun updateData(name: String, sex: String, age: Int){
        // Launching room database connection
        MainScope().launch {
            // Creating connection to database
            val userDao = Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                .fallbackToDestructiveMigration().build().userDao()
            val user = userDao.getUser()[0]

            // Arguments to Post Request
            val formBody: RequestBody = FormBody.Builder()
                .add("name", name)
                .add("edad", age.toString())
                .add("sex", sex)
                .build()

            // TODO("Verify if token is valid, if not, change it")
            // Making http request
            val request = Request.Builder()
                .url("https://apiskydelight.herokuapp.com/usuarios/actualizar-informacion/")
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

                    // Launching room database connection
                    MainScope().launch {
                        // Updating user info in local database
                        userDao.updateUser(User(user.email, name, sex, age, user.token, user.refresh, user.session, user.advice))

                        // Showing succesful dialog
                        val dialog = MaterialAlertDialogBuilder(findNavController().context)
                            .setTitle("¡Actualización Exitosa!")
                            .setMessage("¡Tus Datos se Actualizaron Correctamente!")
                            .setCancelable(false)
                            .show()
                        Handler(Looper.getMainLooper()).postDelayed({
                            dialog.dismiss()

                            // Fragment enters from right
                            (parentFragment as NavBarFragment).updateNavBarHost(ProfileFragment(), R.id.nav_profile, false)
                        }, 5000)
                    }
                }

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

    // Function to connect with the database
    private fun showUserData(){
        // Launching room database connection
        MainScope().launch {
            // Creating connection to database
            val user = Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                .fallbackToDestructiveMigration().build().userDao().getUser()[0]

            // Setting screen data
            binding.editTxtName.setText(user.name)
            binding.numberPickerAge.value = user.age

            if (binding.btnMale.text.toString() == user.sex)
                binding.btnMale.isChecked = true
            else
                binding.btnFemale.isChecked = true
        }
    }
}