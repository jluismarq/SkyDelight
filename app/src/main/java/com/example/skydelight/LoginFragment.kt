package com.example.skydelight

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentLoginBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
            // Connection to the api and sending the password to the email
            else {
                // TODO("How to stay logged in")
                // TODO("Connection to the Api to verify account and login")
                Toast.makeText(findNavController().context, "Aceptado", Toast.LENGTH_LONG).show()
            }
        }

        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_startScreen)
            findNavController().popBackStack(R.id.login_fragment, true) }
    }
}