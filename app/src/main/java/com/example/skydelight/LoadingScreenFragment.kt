package com.example.skydelight

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import pl.bclogic.pulsator4droid.library.PulsatorLayout
import java.io.File
import java.io.FileWriter

class LoadingScreenFragment : Fragment() {
    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading_screen, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // initialise pulsator
        view.findViewById<PulsatorLayout>(R.id.pulsator).start()

        // Start heart beat pulse
        val pulse = AnimationUtils.loadAnimation(findNavController().context, R.anim.pulse)
        view.findViewById<ImageView>(R.id.imgHeartLogo).startAnimation(pulse)

        // Changing to the start fragment after random time delay
        Handler(Looper.getMainLooper()).postDelayed({
            // Clearing file if it has content
            if(File(activity?.getExternalFilesDir(null), "usr_session.txt").exists())
                findNavController().navigate(R.id.action_loadingScreen_to_principal)
            else
                findNavController().navigate(R.id.action_loadingScreen_to_startScreen)
        }, (2500..7500).shuffled().last().toLong())
    }
}