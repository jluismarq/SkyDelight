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
import com.example.skydelight.databinding.FragmentRegisterSecondBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val NAME_PARAM = "name"
private const val BIRTHDAY_PARAM = "birthday"
private const val SEX_PARAM = "sex"

class RegisterSecondFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding: FragmentRegisterSecondBinding
    private var name: String? = null
    private var birthday: String? = null
    private var sex: String? = null

    // Getting parameters
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            name = it.getString(NAME_PARAM)
            birthday = it.getString(BIRTHDAY_PARAM)
            sex = it.getString(SEX_PARAM)
        }
    }

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
            // Connection to the api and sending the password to the email
            else {
                // TODO("Connection to the Api to create account")
                Toast.makeText(findNavController().context, "Aceptado", Toast.LENGTH_LONG).show()
            }
        }

        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {findNavController().navigate(R.id.action_registerSecond_to_registerFirst)}
    }

    //Function to receive data from another fragments
    companion object {
        fun newInstance(name: String, birthday: String, sex: String) =
            RegisterSecondFragment().apply {
                arguments = Bundle().apply {
                    putString(NAME_PARAM, name)
                    putString(BIRTHDAY_PARAM, birthday)
                    putString(SEX_PARAM, sex)
                }
            }
    }
}