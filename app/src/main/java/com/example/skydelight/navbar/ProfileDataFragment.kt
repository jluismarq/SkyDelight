package com.example.skydelight.navbar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.skydelight.custom.User
import com.example.skydelight.custom.ValidationsDialogsRequests
import com.example.skydelight.databinding.FragmentNavbarProfileDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*

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

            // Connection to the api and sending the password to the email
            if(ValidationsDialogsRequests().validateName(name, binding.FieldName)
                && ValidationsDialogsRequests().validateSex(sexId, findNavController().context,
                    "¡Error! ¡Elemento Faltante!", "¡Ups! ¡Parece que olvidaste elegir tu sexo!"))
                updateData(name, sex, age)
        }
    }

    // Function to connect with the api
    private fun updateData(name: String, sex: String, age: Int){
        // Deactivating clickable
        (parentFragment as NavBarFragment).changeNavBarButtonsClickable(false)

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

            ValidationsDialogsRequests().httpPetition(request, findNavController().context, requireActivity(), binding.btnUpdate,
                binding.btnCancel, null, null, getString(R.string.loadingDialog_updating), null, null, null,
                (parentFragment as NavBarFragment))
            {
                // Launching room database connection
                MainScope().launch {
                    // Updating user info in local database
                    userDao.updateUser(User(user.email, name, sex, age, user.token, user.refresh, user.session, user.advice))

                    // Showing succesful dialog
                    ValidationsDialogsRequests().dialogOnUIThread("¡Actualización Exitosa!",
                        "¡Tus Datos se Actualizaron Correctamente!", findNavController().context,
                        requireActivity(), null, null, null, null, null) {
                        // Fragment enters from right
                        (parentFragment as NavBarFragment).updateNavBarHost(ProfileFragment(), R.id.nav_profile, false)
                    }
                }
            }
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