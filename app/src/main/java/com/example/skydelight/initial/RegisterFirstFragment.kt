package com.example.skydelight.initial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.navigation.fragment.findNavController
import com.example.skydelight.R
import com.example.skydelight.custom.ValidationsDialogsRequests
import com.example.skydelight.databinding.FragmentRegisterFirstBinding

private const val NAME_PARAM = "name"
private const val AGE_PARAM = "age"
private const val SEX_PARAM = "sex"

class RegisterFirstFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding: FragmentRegisterFirstBinding

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
        binding = FragmentRegisterFirstBinding.inflate(inflater, container, false)
        return binding.root

        //return inflater.inflate(R.layout.fragment_register_first, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Changing Default Values of Age Number Picker
        binding.numberPickerAge.minValue = 18
        binding.numberPickerAge.maxValue = 25
        binding.numberPickerAge.value = 20

        // Setting values if user comes from register second fragment
        if(!name.isNullOrEmpty() || !age.isNullOrEmpty() || !sex.isNullOrEmpty()) {
            binding.editTxtName.setText(name)
            binding.numberPickerAge.value = age!!.toInt()

            if (binding.btnMale.text.toString() == sex)
                binding.btnMale.isChecked = true
            else
                binding.btnFemale.isChecked = true
        }

        // Clearing errors when produced
        binding.editTxtName.doOnTextChanged { _, _, _, _ -> if(binding.FieldName.error != null) binding.FieldName.error = null }

        binding.btnNext.setOnClickListener {
            // Getting sex option selected
            val sexId = binding.radioGroupSex.checkedRadioButtonId

            // Getting user answers
            name = binding.editTxtName.text.toString()
            age = binding.numberPickerAge.value.toString()
            sex = binding.radioGroupSex.findViewById<RadioButton>(sexId)?.text.toString()


            if(ValidationsDialogsRequests().validateName(name.toString(), binding.FieldName)
                && ValidationsDialogsRequests().validateSex(sexId, findNavController().context,
                    "??Error! ??Elemento Faltante!", "??Ups! ??Parece que olvidaste elegir tu sexo!")){
                // Setting parameters for the next fragment
                val bundle = bundleOf(NAME_PARAM to name, SEX_PARAM to sex, AGE_PARAM to age)

                // Starting next fragment
                findNavController().navigate(R.id.action_registerFirst_to_registerSecond, bundle)
            }
        }

        // Returning to the start screen fragment
        binding.btnReturn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFirst_to_startScreen)
            findNavController().popBackStack(R.id.register_first_fragment, true)
        }
    }
}