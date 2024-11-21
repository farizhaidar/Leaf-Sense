package com.bangkit.leafsense.ui.profil

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.leafsense.R
import com.bangkit.leafsense.data.UserPreference
import com.bangkit.leafsense.databinding.FragmentProfilBinding
import com.bangkit.leafsense.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class ProfilFragment : Fragment(R.layout.fragment_profil) {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(UserPreference.getInstance(requireContext()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfilBinding.bind(view)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName ?: "Unknown"
        val userEmail = currentUser?.email ?: "Unknown Email"

        binding.nameDisplay.text = userName
        binding.emailDisplay.text = userEmail

        binding.btnlogout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
