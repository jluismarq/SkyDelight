package com.example.skydelight

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentRegisterFirstBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

private const val NAME_PARAM = "name"
private const val AGE_PARAM = "age"
private const val SEX_PARAM = "sex"

class RegisterFirstFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding: FragmentRegisterFirstBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentRegisterFirstBinding.inflate(inflater, container, false)
        return binding.root

        //return inflater.inflate(R.layout.fragment_register_first, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Changing Values of Number Picker
        binding.numberPickerAge.minValue = 18
        binding.numberPickerAge.maxValue = 25
        binding.numberPickerAge.value = 20

        binding.btnNext.setOnClickListener {
            val name = binding.editTxtName.text.toString()
            val sexId = binding.radioGroupSex.checkedRadioButtonId
            val age = binding.numberPickerAge.value.toString()

            // Showing alert dialog if email field is empty
            if(name.isEmpty()){
                val dialog = MaterialAlertDialogBuilder(findNavController().context)
                    .setTitle("¡Error! ¡Campo Vacío!")
                    .setMessage("¡Ups! ¡Parece que olvidaste colocar tu nombre!")
                    .show()
                Handler(Looper.getMainLooper()).postDelayed({
                    dialog.dismiss()
                }, 5000)
            }
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
            // Sending variables to the next register fragment
            else {
                val sex = binding.radioGroupSex.findViewById<RadioButton>(sexId).text.toString()

                // Setting parameters for the next fragment
                val bundle = bundleOf(NAME_PARAM to name, SEX_PARAM to sex, AGE_PARAM to age)

                // Starting next fragment
                findNavController().navigate(R.id.action_registerFirst_to_registerSecond, bundle)
            }
        }

        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {
            // TODO("Clean Back Stack to all the fragments")
            findNavController().navigate(R.id.action_registerFirst_to_startScreen)
            findNavController().popBackStack(R.id.register_first_fragment, true) }
    }
}