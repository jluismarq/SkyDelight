package com.example.skydelight.navbar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.databinding.FragmentNavbarHomeBinding
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
            val userDao = Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user")
                .fallbackToDestructiveMigration().build().userDao()
            val user = userDao.getUser()[0]

            // TODO("Verify test answers to omit specific advices")
            // Arguments to Post Request
            val formBody: RequestBody = FormBody.Builder()
                .add("list_excluded", "[]")
                .add("type_advice", if (user.advice) "general" else "especifico")
                .build()

            // TODO("Verify if token is valid, if not, change it")
            // Making http request
            val request = Request.Builder().url("https://apiskydelight.herokuapp.com/api/consejo").post(formBody).build()

            // Toggle advice boolean
            user.advice = !user.advice
            userDao.updateUser(user)

            // Making HTTP request and getting response
            OkHttpClient().newCall(request).enqueue(object : Callback {
                // Changing to principal fragment if it's successful
                override fun onResponse(call: Call, response: Response){
                    // Printing api answer
                    val responseString = response.body()?.string().toString()
                    Log.d("OKHTTP3-CODE", response.code().toString())
                    Log.d("OKHTTP3-BODY", responseString)

                    // Code 200 = advice generated
                    if(response.code() in 200..202) {
                        // Changing http body to json
                        val json = JSONObject(responseString)
                        binding.textView.text = json.getString("consejo")
                    }
                    // Connection or server errors
                    else if(response.code() in 400..405 || response.code() == 500)
                        binding.textView.text = getString(R.string.loadingDialog_error)
                }

                // Print dialog if it's error
                override fun onFailure(call: Call, e: IOException){
                    binding.textView.text = getString(R.string.loadingDialog_error)

                    // Printing api answer
                    Log.d("OKHTTP3-ERROR", e.toString())
                }
            })
        }
    }
}