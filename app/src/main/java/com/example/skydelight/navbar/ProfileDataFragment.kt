package com.example.skydelight.navbar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.databinding.FragmentNavbarProfileDataBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
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
        binding.editTxtEmail.doOnTextChanged { _, _, _, _ -> if(binding.FieldEmail.error != null) binding.FieldEmail.error = null }
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
            val email = binding.editTxtEmail.text.toString()
            val age = binding.numberPickerAge.value.toString()
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
            // Showing alert dialog if email field is empty
            else if(email.isEmpty())
                binding.FieldEmail.error = "Olvidaste colocar tu correo"
            // Showing alert dialog if email field is not an email
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                binding.FieldEmail.error = "Formato de correo no válido"
            // Showing alert dialog if email has more than 50 characters
            else if(email.length > 50)
                binding.FieldEmail.error = "La longitud máxima es de 50 caracteres"
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
            else updateData(name, email, sex, age)
        }
    }

    // TODO("Connection to the api to update data")
    // TODO("Update local database")
    private fun updateData(name: String, email: String, sex: String, age: String){
        // Fragment enters from right
        (parentFragment as NavBarFragment).updateNavBarHost(ProfileFragment(), R.id.nav_profile, false)
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
            binding.editTxtEmail.setText(user.email)
            binding.numberPickerAge.value = user.age

            if (binding.btnMale.text.toString() == user.sex)
                binding.btnMale.isChecked = true
            else
                binding.btnFemale.isChecked = true
        }
    }
}