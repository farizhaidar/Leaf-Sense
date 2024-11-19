package com.bangkit.leafsense.ui.home

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bangkit.leafsense.R
import com.bangkit.leafsense.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    // Declare the ViewBinding object
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the binding object
        _binding = FragmentHomeBinding.bind(view)

        // Retrieve the username from SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("userName", "User") // Default value is "User"


        binding.userText.text = "Halo $userName!" // Menampilkan "Halo (user)!"

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
