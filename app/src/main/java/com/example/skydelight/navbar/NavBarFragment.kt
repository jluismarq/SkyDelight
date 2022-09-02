package com.example.skydelight.navbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.skydelight.R
import com.example.skydelight.databinding.FragmentNavbarBinding

class NavBarFragment : Fragment() {
    // Binding variable to use elements in the xml layout
    private lateinit var binding : FragmentNavbarBinding

    // Creating the fragment view
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentNavbarBinding.inflate(inflater, container, false)

        return binding.root

        // return inflater.inflate(R.layout.fragment_navbar, container, false)
    }

    // Variable to save actual fragment
    private var itemId = R.id.nav_home

    // After the view is created we can do things
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bottom navigation bar actions
        binding.navBar.setOnItemSelectedListener{
            when(it.itemId){
                R.id.nav_home -> {
                    // Changing fragment if actual fragment is not the same
                    // Fragment enters from left
                    if(itemId != R.id.nav_home)
                        updateNavBarHost(HomeFragment(), R.id.nav_home, false)
                    true
                }
                R.id.nav_test -> {
                    // Changing fragment if actual fragment is not the same
                    when (itemId) {
                        // Fragment enters from right
                        R.id.nav_home -> updateNavBarHost(TestFragment(), R.id.nav_test, true)

                        // Fragment enters from left
                        R.id.nav_games -> updateNavBarHost(TestFragment(), R.id.nav_test, false)

                        // Fragment enters from left
                        R.id.nav_profile -> updateNavBarHost(TestFragment(), R.id.nav_test, false)
                    }
                    true
                }
                R.id.nav_games -> {
                    // Changing fragment if actual fragment is not the same
                    when (itemId) {
                        // Fragment enters from right
                        R.id.nav_home -> updateNavBarHost(GamesFragment(), R.id.nav_games, true)

                        // Fragment enters from right
                        R.id.nav_test -> updateNavBarHost(GamesFragment(), R.id.nav_games, true)

                        // Fragment enters from left
                        R.id.nav_profile -> updateNavBarHost(GamesFragment(), R.id.nav_games, false)
                    }
                    true
                }
                R.id.nav_profile -> {
                    // Changing fragment if actual fragment is not the same
                    // Fragment enters from right
                    if(itemId != R.id.nav_profile)
                        updateNavBarHost(ProfileFragment(), R.id.nav_profile, true)
                    true
                }
                else -> false
            }
        }
    }

    // Function to change fragment of navbar host
    fun updateNavBarHost(fragment : Fragment, navId: Int, direction: Boolean){
        // Variable to change navigation bar fragments
        val transaction = childFragmentManager.beginTransaction()

        // Fragment enters from right
        if(direction)
            transaction.setCustomAnimations(
                R.anim.slide_from_right, R.anim.slide_exit_to_left,
                R.anim.slide_from_left, R.anim.slide_exit_to_right
            )
        // Fragment enters from left
        else
            transaction.setCustomAnimations(
                R.anim.slide_from_left, R.anim.slide_exit_to_right,
                R.anim.slide_from_right, R.anim.slide_exit_to_left
            )

        // Changing fragment and actual fragment id
        transaction.replace(binding.navbarHostFragment.id, fragment).commit()
        itemId = navId
    }
}