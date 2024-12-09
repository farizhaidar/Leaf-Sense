package com.bangkit.leafsense.ui.profil

import android.app.DatePickerDialog
import android.content.Intent
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfilFragment : Fragment(R.layout.fragment_profil) {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(UserPreference.getInstance(requireContext()))
    }

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var isEditMode: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfilBinding.bind(view)

        getUserProfileData()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userName = currentUser?.displayName ?: "Tidak dikenal"
        val userEmail = currentUser?.email ?: "Email tidak dikenal"

        binding.nameDisplay.text = userName
        binding.emailDisplay.text = userEmail

        binding.btnLogout.setOnClickListener {
            if (isEditMode) {
                saveUserProfileData()
            } else {
                profileViewModel.logout()
                val intent = Intent(requireContext(), LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        binding.btnEditProfile.setOnClickListener {
            toggleEditMode()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            binding.btnLogout.text = "Simpan"
            binding.btnEditProfile.visibility = View.GONE

            binding.nameEdit.setText(binding.nameDisplay.text.toString())
            binding.emailEdit.setText(binding.emailDisplay.text.toString())
            binding.ageEdit.setText(binding.ageDisplay.text.toString()) // Pastikan tanggal tidak hilang
            binding.jobEdit.setText(binding.jobDisplay.text.toString())

            binding.nameEdit.visibility = View.VISIBLE
            binding.ageEdit.visibility = View.VISIBLE
            binding.jobEdit.visibility = View.VISIBLE

            binding.nameDisplay.visibility = View.GONE
            binding.ageDisplay.visibility = View.GONE
            binding.jobDisplay.visibility = View.GONE

        } else {
            binding.btnLogout.text = "Keluar"
            binding.btnEditProfile.visibility = View.VISIBLE

            binding.nameDisplay.visibility = View.VISIBLE
            binding.ageDisplay.visibility = View.VISIBLE
            binding.jobDisplay.visibility = View.VISIBLE

            binding.nameEdit.visibility = View.GONE
            binding.ageEdit.visibility = View.GONE
            binding.jobEdit.visibility = View.GONE
        }
    }

    private fun saveUserProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = firestore.collection("users").document(userId)

            val updatedData: Map<String, Any> = mapOf(
                "name" to binding.nameEdit.text.toString(),
                "email" to binding.emailEdit.text.toString(),
                "age" to binding.ageEdit.text.toString(),
                "job" to binding.jobEdit.text.toString()
            )

            userRef.update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()

                    // Perbarui tampilan dengan data baru
                    binding.nameDisplay.text = binding.nameEdit.text.toString()
                    binding.emailDisplay.text = binding.emailEdit.text.toString()
                    binding.ageDisplay.text = binding.ageEdit.text.toString()
                    binding.jobDisplay.text = binding.jobEdit.text.toString()

                    toggleEditMode()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Gagal memperbarui profil: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun getUserProfileData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val userRef = firestore.collection("users").document(userId)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (isAdded) {
                        if (document.exists()) {
                            val userName = document.getString("name") ?: "Nama tidak tersedia"
                            val userEmail = document.getString("email") ?: "Email tidak tersedia"
                            val userAge = document.getString("age") ?: "Usia tidak tersedia"
                            val userJob = document.getString("job") ?: "Pekerjaan tidak tersedia"

                            binding.nameDisplay.text = userName
                            binding.emailDisplay.text = userEmail
                            binding.ageDisplay.text = userAge
                            binding.jobDisplay.text = userJob

                            binding.nameEdit.setText(userName)
                            binding.emailEdit.setText(userEmail)
                            binding.ageEdit.setOnClickListener {
                                showDatePicker()
                            }
                            binding.jobEdit.setText(userJob)
                        } else {
                            Toast.makeText(requireContext(), "Data pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Gagal mengambil data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        // Jika EditText sudah memiliki data, gunakan sebagai tanggal awal
        val existingDate = binding.ageEdit.text.toString()
        if (existingDate.isNotEmpty()) {
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            try {
                calendar.time = dateFormat.parse(existingDate) ?: Calendar.getInstance().time
            } catch (e: Exception) {
                // Jika parsing gagal, tetap gunakan tanggal sekarang
                calendar.time = Calendar.getInstance().time
            }
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDay)
            }
            val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
            binding.ageEdit.setText(dateFormat.format(selectedDate.time))
        }, year, month, day).show()
    }

    private fun calculateAge(birthDate: Calendar): Int {
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
