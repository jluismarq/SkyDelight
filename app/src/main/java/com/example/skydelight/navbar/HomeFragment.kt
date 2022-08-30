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
        showAdvice()
    }

    // Function to connect with the api
    private fun showAdvice() {
        // Launching room database connection
        MainScope().launch {
            // Creating connection to database
            val userDao = Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user").build().userDao().getUser()[0]

            // TODO("Change type of advice in each petition with a boolean flag")
            // Arguments to Post Request
            val formBody: RequestBody = FormBody.Builder()
                .add("list_excluded", "[]")
                .add("type_advice", "general")
                .build()

            // TODO("Verify if token is valid, if not, change it")
            // Making http request
            val request = Request.Builder()
                .url("https://apiskydelight.herokuapp.com/api/consejo")
                .post(formBody)
                .addHeader("Authorization", "Bearer " + userDao.token)
                .addHeader("KEY-CLIENT", BuildConfig.API_KEY)
                .build()

            // Showing loading dialog
            val customDialog = CustomLoadingDialog(findNavController().context, getString(R.string.loadingDialog_loading))
            customDialog.show()

            // Making HTTP request and getting response
            OkHttpClient().newCall(request).enqueue(object : Callback {
                // Changing to principal fragment if it's successful
                override fun onResponse(call: Call, response: Response){
                    // Closing loading dialog
                    customDialog.dismiss()

                    // Printing api answer
                    val responseString = response.body()?.string().toString()
                    Log.d("OKHTTP3-CODE", response.code().toString())
                    Log.d("OKHTTP3-BODY", responseString)

                    // Changing http body to json
                    val json = JSONObject(responseString)
                    binding.textView.text = json.getString("consejo")
                }

                // Print dialog if it's error
                override fun onFailure(call: Call, e: IOException){
                    // Closing loading dialog
                    customDialog.dismiss()

                    // Printing api answer
                    Log.d("OKHTTP3-ERROR", e.toString())
                }
            })
        }
    }
}