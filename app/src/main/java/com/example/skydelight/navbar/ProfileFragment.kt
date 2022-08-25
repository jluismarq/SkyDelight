package com.example.skydelight.navbar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentNavbarProfileBinding

// TODO("Screen design")
// TODO("Connection to local room db to get user info")
// TODO("Connection to local room db to close session")
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
    }
}