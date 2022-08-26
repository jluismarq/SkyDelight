package com.example.skydelight.navbar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.example.skydelight.custom.CustomLoadingDialog
import com.example.skydelight.custom.User
import com.example.skydelight.databinding.FragmentNavbarHomeBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

// TODO("Screen design")
// TODO("Connection to the api to get advices")
class HomeFragment : Fragment() {

    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentNavbarHomeBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentNavbarHomeBinding.inflate(inflater, container, false)
        return binding.root

        // return inflater.inflate(R.layout.fragment_navbar_home, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Showing initial random advice
        //showAdvice()
    }

    // Function to connect with the api
    /*private fun showAdvice() {
        // Making http request
        val request = Request.Builder()
            .url("https://apiskydelight.herokuapp.com/api/consejo/")
            .header("KEY-CLIENT", BuildConfig.API_KEY)
            .build()

        // Making HTTP request and getting response
        OkHttpClient().newCall(request).enqueue(object : Callback {
            // Changing to principal fragment if it's successful
            override fun onResponse(call: Call, response: Response){
                // Printing api answer
                val responseString = response.body()?.string().toString()
                Log.d("OKHTTP3-CODE", response.code().toString())
                Log.d("OKHTTP3-BODY", responseString)
            }

            // Print dialog if it's error
            override fun onFailure(call: Call, e: IOException){
                // Printing api answer
                Log.d("OKHTTP3-ERROR", e.toString())
            }
        })
    }*/
}