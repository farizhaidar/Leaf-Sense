package com.bangkit.leafsense.ui.profil

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.bangkit.leafsense.databinding.FragmentProfilBinding
import com.bangkit.leafsense.ui.login.LoginActivity
import com.bangkit.leafsense.data.UserPreference

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userPreference = UserPreference(requireContext())
        val factory = ProfileViewModelFactory(userPreference)
        profileViewModel = ViewModelProvider(this, factory).get(ProfileViewModel::class.java)

        binding.btnlogout.setOnClickListener {
            profileViewModel.logout()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
