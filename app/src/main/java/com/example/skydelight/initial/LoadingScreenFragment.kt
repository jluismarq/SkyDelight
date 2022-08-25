package com.example.skydelight.initial

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
import androidx.room.Room
import com.example.skydelight.R
import com.example.skydelight.custom.AppDatabase
import com.example.skydelight.custom.User
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import pl.bclogic.pulsator4droid.library.PulsatorLayout
import java.io.File

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
            // Launching room database connection
            MainScope().launch {
                // Creating connection to database
                val userDao = Room.databaseBuilder(findNavController().context, AppDatabase::class.java, "user").build().userDao()

                // If user exists, changing to principal fragments
                if(userDao.getUser().isNotEmpty())
                    findNavController().navigate(R.id.action_loadingScreen_to_navBar)
                // Else changing to initial fragments
                else
                    findNavController().navigate(R.id.action_loadingScreen_to_startScreen)
            }
        }, (2500..7500).shuffled().last().toLong())
    }
}