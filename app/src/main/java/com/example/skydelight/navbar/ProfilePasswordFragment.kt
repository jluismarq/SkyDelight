package com.example.skydelight.navbar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.skydelight.R
import com.example.skydelight.databinding.FragmentNavbarProfilePasswordBinding

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
            if(password.isEmpty())
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
            // Showing alert dialog if confirm password field is empty
            else if(confirmedPassword.isEmpty())
                binding.FieldConfirmPassword.error = "Olvidaste confirmar tu contraseña"
            // Showing alert dialog if password and confirmedPassword don't match
            else if(password != confirmedPassword)
                binding.FieldConfirmPassword.error = "Esta contraseña es distinta a la primera"
            // Connection to the api and creation of the new user
            else updatePassword()
        }
    }

    // TODO("Connection to the api to update password")
    // TODO("Update local database")
    private fun updatePassword(){
        // Starting previous fragment
        findNavController().navigate(R.id.action_navBarProfilePassword_to_navBarProfile)
    }
}