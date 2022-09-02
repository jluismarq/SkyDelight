package com.example.skydelight.navbar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.custom.ValidationsDialogsRequests
import com.example.skydelight.databinding.FragmentNavbarProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*

class ProfileFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentNavbarProfileBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentNavbarProfileBinding.inflate(inflater, container, false)
        return binding.root

        // return inflater.inflate(R.layout.fragment_navbar_profile, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Showing user data in profile screen fragment
        showUserData()

        // Changing to updating data fragment
        binding.btnUpdateAccount.setOnClickListener {
            // Fragment enters from right
            (parentFragment as NavBarFragment).updateNavBarHost(ProfileDataFragment(), R.id.nav_profile, true)
        }

        // Changing to updating password fragment
        binding.btnChangePassword.setOnClickListener {
            // Fragment enters from right
            (parentFragment as NavBarFragment).updateNavBarHost(ProfilePasswordFragment(), R.id.nav_profile, true)
        }

        // Returning to the start screen fragment
        binding.btnCloseSession.setOnClickListener {
            // Launching room database connection
            MainScope().launch {
                // Cleaning database and changing to start screen fragment
                Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                    .fallbackToDestructiveMigration().build().userDao().deleteUsers()
                findNavController().navigate(R.id.action_navBar_to_startScreen)
            }
        }

        // Returning to the start screen fragment
        binding.btnDeleteAccount.setOnClickListener {
            MaterialAlertDialogBuilder(findNavController().context)
                .setTitle("¡Cuidado!")
                .setMessage("¿Realmente Quieres Eliminar tu Cuenta?")
                .setCancelable(false)
                .setNeutralButton("¡No!"){ dialog, _ -> dialog.dismiss() }
                .setPositiveButton("¡Sí!"){ dialog, _ ->
                    dialog.dismiss()

                    // Deactivating clickable
                    (parentFragment as NavBarFragment).changeNavBarButtonsClickable(false)

                    // Launching room database connection
                    MainScope().launch {
                        // Creating connection to database
                        val userDao = Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                            .fallbackToDestructiveMigration().build().userDao()
                        val user = userDao.getUser()[0]

                        // TODO("Verify if token is valid, if not, change it")
                        // Making http request
                        val request = Request.Builder()
                            .url("https://apiskydelight.herokuapp.com/usuarios/eliminar-usuario/")
                            .put(FormBody.Builder().add("email", user.email).build())
                            .addHeader("Authorization", "Bearer " + user.token)
                            .addHeader("KEY-CLIENT", BuildConfig.API_KEY)
                            .build()

                        ValidationsDialogsRequests().httpPetition(request, findNavController().context, requireActivity(),
                            binding.btnUpdateAccount, binding.btnChangePassword, binding.btnCloseSession,
                            binding.btnDeleteAccount, getString(R.string.loadingDialog_updating), null,
                            null, null, (parentFragment as NavBarFragment))
                        {
                            // Launching room database connection
                            MainScope().launch {
                                // Cleaning database and changing to start screen fragment
                                userDao.deleteUsers()
                                findNavController().navigate(R.id.action_navBar_to_startScreen)
                            }
                        }
                    }
                }.show()
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
            binding.txtEmailUser.text = user.email
            binding.txtNameUser.text = user.name
            binding.txtSexUser.text = user.sex
            binding.txtAgeUser.text = user.age.toString()
        }
    }
}