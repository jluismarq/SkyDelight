package com.example.skydelight

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentRecoverPasswordBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RecoverPasswordFragment : Fragment() {

    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentRecoverPasswordBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentRecoverPasswordBinding.inflate(inflater, container, false)
        return binding.root

        //return inflater.inflate(R.layout.fragment_recover_password, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRecover.setOnClickListener {
            val email = binding.editTxtEmail.text

            // Showing alert dialog if email field is empty
            if(email.isNullOrEmpty()){
                MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Campo Vacío!")
                    .setMessage("¡Ups! ¡Parece que olvidaste colocar tu correo!")
                    .setPositiveButton("¡Entendido!"){ dialog, which -> dialog.dismiss()}
                    .show()
            }
        }

        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {findNavController().navigate(R.id.action_recoverPassword_to_startScreen)}
    }
}