package com.example.skydelight

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LoadingScreenFragment : Fragment() {
    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading_screen, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO("Personalize loading screen")
        // TODO("Use transitions between fragments")
        // Changing to the start fragment after a delay
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_loadingScreen_to_startScreen) }, 2000)
    }
}