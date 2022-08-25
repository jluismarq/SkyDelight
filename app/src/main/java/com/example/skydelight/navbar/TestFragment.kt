package com.example.skydelight.navbar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.skydelight.databinding.FragmentNavbarTestBinding

// TODO("Screen design")
// TODO("Add test fragment")
// TODO("Verify that fragment keeps alive when changing section, if not, create db class to save test type, answers and last question")
// TODO("Connection to the api to create test and save answers when user finishes")
class TestFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentNavbarTestBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentNavbarTestBinding.inflate(inflater, container, false)
        return binding.root

        // return inflater.inflate(R.layout.fragment_navbar_test, container, false)
    }

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}