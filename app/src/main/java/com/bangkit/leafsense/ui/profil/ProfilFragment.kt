package com.bangkit.leafsense.ui.profil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bangkit.leafsense.R
import com.bangkit.leafsense.data.UserPreference
import com.bangkit.leafsense.databinding.FragmentProfilBinding
import com.bangkit.leafsense.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfilFragment : Fragment(R.layout.fragment_profil) {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(UserPreference.getInstance(requireContext()))
    }

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfilBinding.bind(view)

        getUserProfileData()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName ?: "Unknown"
        val userEmail = currentUser?.email ?: "Unknown Email"

        binding.nameDisplay.text = userName
        binding.emailDisplay.text = userEmail

        // Menangani tombol logout
        binding.btnlogout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun getUserProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = firestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        // Mengambil data dari Firestore
                        val userName = document.getString("name") ?: "Nama tidak tersedia"
                        val userEmail = document.getString("email") ?: "Email tidak tersedia"
                        val userAge = document.getString("age") ?: "Usia tidak tersedia"
                        val userJob = document.getString("job") ?: "Pekerjaan tidak tersedia"

                        // Menampilkan data di UI
                        binding.nameDisplay.text = userName
                        binding.emailDisplay.text = userEmail
                        binding.AgeDisplay.text = userAge
                        binding.JobDisplay.text = userJob
                    } else {
                        Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
