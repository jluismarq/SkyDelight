package com.example.skydelight.initial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.custom.User
import com.example.skydelight.custom.ValidationsDialogsRequests
import com.example.skydelight.databinding.FragmentRegisterSecondBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject

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

        // Clearing errors when produced
        binding.editTxtEmail.doOnTextChanged { _, _, _, _ -> if(binding.FieldEmail.error != null) binding.FieldEmail.error = null }
        binding.editTxtPassword.doOnTextChanged { _, _, _, _ -> if(binding.FieldPassword.error != null) binding.FieldPassword.error = null }
        binding.editTxtConfirmPassword.doOnTextChanged { _, _, _, _ ->
            if(binding.FieldConfirmPassword.error != null) binding.FieldConfirmPassword.error = null }

        binding.btnCreateAccount.setOnClickListener{
            val email = binding.editTxtEmail.text.toString()
            val password = binding.editTxtPassword.text.toString()
            val confirmedPassword = binding.editTxtConfirmPassword.text.toString()

            if(ValidationsDialogsRequests().validateEmail(email, binding.FieldEmail)
                && ValidationsDialogsRequests().validatePassword(password, binding.FieldPassword)
                && ValidationsDialogsRequests().validateConfirmedPassword(password, confirmedPassword, binding.FieldConfirmPassword)){
                // Deactivating buttons
                binding.btnCreateAccount.isClickable = false
                binding.btnReturn.isClickable = false
                createUser(email, password, name.toString(), sex.toString(), age.toString())
            }
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

        ValidationsDialogsRequests().httpPetition(request, findNavController().context, requireActivity(),
            binding.btnCreateAccount, binding.btnReturn, null, null, getString(R.string.loadingDialog_creating), 400,
            "??Error! ??Cuenta Existente!","??Ya existe una cuenta con ese correo!",null)
        {
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

            ValidationsDialogsRequests().httpPetition(request, findNavController().context, requireActivity(),
                binding.btnCreateAccount, binding.btnReturn, null, null, null,null,
                null, null, null)
            { responseString: String ->
                // Changing http body to json
                val json = JSONObject(responseString)

                // Launching room database connection
                MainScope().launch {
                    // Creating connection to database
                    val userDao = Room.databaseBuilder(findNavController().context,
                        AppDatabase::class.java,"user").fallbackToDestructiveMigration().build().userDao()

                    // If user exists, we have to delete it
                    val user = userDao.getUser()
                    if (user.isNotEmpty())
                        userDao.deleteUser(user[0])

                    // Adding the new user to the database
                    userDao.insertUser(User(json.getString("user"),
                        json.getString("name"), json.getString("sex"),
                        json.getInt("age"), json.getString("access"),
                        json.getString("refresh"),true))

                    ValidationsDialogsRequests().dialogOnUIThread("??Registro Exitoso!",
                        "??Tu cuenta ha sido creada correctamente!", findNavController().context,
                        requireActivity(), null, null, null, null, null) {
                        findNavController().navigate(R.id.action_registerSecond_to_registerThird)
                    }
                }
            }
        }
    }
}