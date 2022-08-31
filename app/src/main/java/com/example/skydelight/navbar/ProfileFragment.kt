package com.example.skydelight.navbar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.BuildConfig
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.databinding.FragmentNavbarProfileBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

// TODO("Screen design")
// TODO("Connection to local room db to get user info")
// TODO("Connection to local room db to close session")
// TODO("Connection to the api to update user profile")
// TODO("Connection to the api to update password")
// TODO("Connection to the api to delete user profile")
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

        // TODO("Open dialog to ask if user is sure to delete account")
        // Returning to the start screen fragment
        binding.btnDeleteAccount.setOnClickListener {
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

                // Making HTTP request and getting response
                OkHttpClient().newCall(request).enqueue(object : Callback {
                    // Changing to principal fragment if it's successful
                    override fun onResponse(call: Call, response: Response){
                        // Printing api answer
                        val responseString = response.body()?.string().toString()
                        Log.d("OKHTTP3-CODE", response.code().toString())
                        Log.d("OKHTTP3-BODY", responseString)

                        // Launching room database connection
                        MainScope().launch {
                            // Cleaning database and changing to start screen fragment
                            userDao.deleteUsers()
                            findNavController().navigate(R.id.action_navBar_to_startScreen)
                        }
                    }

                    // Print dialog if it's error
                    override fun onFailure(call: Call, e: IOException){
                        // Printing api answer
                        Log.d("OKHTTP3-ERROR", e.toString())
                    }
                })
            }
        }
    }
}