package com.example.skydelight.navbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.skydelight.MainActivity
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
            // Variable to change navigation bar fragments
            val transaction = childFragmentManager.beginTransaction()

            when(it.itemId){
                R.id.nav_home -> {
                    // Changing fragment if actual fragment is not the same
                    // Fragment enters from right
                    if(itemId != R.id.nav_home) {
                        transaction.setCustomAnimations(
                            R.anim.slide_from_left, R.anim.slide_exit_to_right,
                            R.anim.slide_from_right, R.anim.slide_exit_to_left
                        )
                        transaction.replace(binding.navbarHostFragment.id, HomeFragment()).commit()
                        itemId = R.id.nav_home
                    }
                    true
                }
                R.id.nav_test -> {
                    // Changing fragment if actual fragment is not the same
                    when (itemId) {
                        // Fragment enters from right
                        R.id.nav_home -> {
                            transaction.setCustomAnimations(
                                R.anim.slide_from_right, R.anim.slide_exit_to_left,
                                R.anim.slide_from_left, R.anim.slide_exit_to_right
                            )
                            transaction.replace(binding.navbarHostFragment.id, TestFragment()).commit()
                            itemId = R.id.nav_test
                        }
                        // Fragment enters from left
                        R.id.nav_games -> {
                            transaction.setCustomAnimations(
                                R.anim.slide_from_left, R.anim.slide_exit_to_right,
                                R.anim.slide_from_right, R.anim.slide_exit_to_left
                            )
                            transaction.replace(binding.navbarHostFragment.id, TestFragment()).commit()
                            itemId = R.id.nav_test
                        }
                        // Fragment enters from left
                        R.id.nav_profile -> {
                            transaction.setCustomAnimations(
                                R.anim.slide_from_left, R.anim.slide_exit_to_right,
                                R.anim.slide_from_right, R.anim.slide_exit_to_left
                            )
                            transaction.replace(binding.navbarHostFragment.id, TestFragment()).commit()
                            itemId = R.id.nav_test
                        }
                    }
                    true
                }
                R.id.nav_games -> {
                    // Changing fragment if actual fragment is not the same
                    when (itemId) {
                        // Fragment enters from right
                        R.id.nav_home -> {
                            transaction.setCustomAnimations(
                                R.anim.slide_from_right, R.anim.slide_exit_to_left,
                                R.anim.slide_from_left, R.anim.slide_exit_to_right
                            )
                            transaction.replace(binding.navbarHostFragment.id, GamesFragment()).commit()
                            itemId = R.id.nav_games
                        }
                        // Fragment enters from right
                        R.id.nav_test -> {
                            transaction.setCustomAnimations(
                                R.anim.slide_from_right, R.anim.slide_exit_to_left,
                                R.anim.slide_from_left, R.anim.slide_exit_to_right
                            )
                            transaction.replace(binding.navbarHostFragment.id, GamesFragment()).commit()
                            itemId = R.id.nav_games
                        }
                        // Fragment enters from left
                        R.id.nav_profile -> {
                            transaction.setCustomAnimations(
                                R.anim.slide_from_left, R.anim.slide_exit_to_right,
                                R.anim.slide_from_right, R.anim.slide_exit_to_left
                            )
                            transaction.replace(binding.navbarHostFragment.id, GamesFragment()).commit()
                            itemId = R.id.nav_games
                        }
                    }
                    true
                }
                R.id.nav_profile -> {
                    // Changing fragment if actual fragment is not the same
                    // Fragment enters from left
                    if(itemId != R.id.nav_profile) {
                        transaction.setCustomAnimations(
                            R.anim.slide_from_right, R.anim.slide_exit_to_left,
                            R.anim.slide_from_left, R.anim.slide_exit_to_right
                        )
                        transaction.replace(binding.navbarHostFragment.id, ProfileFragment()).commit()
                        itemId = R.id.nav_profile
                    }
                    true
                }
                else -> false
            }
        }
    }
}