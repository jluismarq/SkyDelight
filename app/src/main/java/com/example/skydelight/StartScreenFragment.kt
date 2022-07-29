package com.example.skydelight

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentStartScreenBinding
//import okhttp3.*
//import java.io.IOException

class StartScreenFragment : Fragment() {

    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentStartScreenBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentStartScreenBinding.inflate(inflater, container, false)
        return binding.root

        // return inflater.inflate(R.layout.fragment_start_screen, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //testing the api
        //run("https://apiskydelight.herokuapp.com/api/lista-testcses26")

        // Changing to the login fragment
        binding.btnLogin.setOnClickListener{findNavController().navigate(R.id.action_startScreen_to_login)}

        // Changing to the register first fragment
        binding.btnRegister.setOnClickListener {findNavController().navigate(R.id.action_startScreen_to_registerFirst)}

        // Changing to the recover password fragment
        binding.txtRecoverPassword.setOnClickListener {findNavController().navigate(R.id.action_startScreen_to_recoverPassword)}
    }

    // Function to connect with the api
    /*private val client = OkHttpClient()
    fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println(e.toString())
            } override fun onResponse(call: Call, response: Response) {
                println(response.body()?.string())
            }
        })
    }*/
}