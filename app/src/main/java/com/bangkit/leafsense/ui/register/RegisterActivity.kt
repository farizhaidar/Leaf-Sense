package com.bangkit.leafsense.ui.register

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.leafsense.databinding.ActivityRegisterBinding
import com.bangkit.leafsense.ui.login.LoginActivity
import com.bangkit.leafsense.Result
import com.google.firebase.auth.FirebaseAuth
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.Observer
import com.bangkit.leafsense.R
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val registerViewModel: RegisterViewModel by viewModels { RegisterViewModelFactory(FirebaseAuth.getInstance()) }

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        registerViewModel.registerResult.observe(this, Observer { result ->
            when (result) {
                is Result.Loading -> {
                    binding.loadingAnimation.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.loadingAnimation.visibility = View.GONE
                    Toast.makeText(
                        this,
                        "Pendaftaran berhasil. Silakan periksa email Anda untuk memverifikasi akun Anda.",
                        Toast.LENGTH_LONG
                    ).show()
                    navigateToVerifyEmailActivity()
                }
                is Result.Error -> {
                    binding.loadingAnimation.visibility = View.GONE
                    Toast.makeText(this, "Pendaftaran gagal: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })


        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val age = binding.etAge.text.toString().trim()
            val job = binding.etJob.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && age.isNotEmpty() && job.isNotEmpty()) {
                if (isValidEmail(email)) {
                    registerUser(name, email, password, age, job)
                } else {
                    Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Semua wajib diisi", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            navigateToLoginActivity()
        }

        val passwordEditText: EditText = binding.etPassword
        val eyeIcon: ImageView = binding.showPasswordButton

        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(isPasswordVisible, passwordEditText, eyeIcon)
        }

        binding.etAge.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun registerUser(name: String, email: String, password: String, age: String, job: String) {
        registerViewModel.register(name, email, password, age, job)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToVerifyEmailActivity() {
        val intent = Intent(this, VerifyEmailActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun togglePasswordVisibility(isPasswordVisible: Boolean, passwordInput: EditText, showPasswordButton: ImageView) {
        if (isPasswordVisible) {
            passwordInput.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            showPasswordButton.setImageResource(R.drawable.hidden) // Use the 'hidden' drawable for visible state
        } else {
            passwordInput.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
            showPasswordButton.setImageResource(R.drawable.eye) // Use the 'eye' drawable for hidden state
        }
        passwordInput.setSelection(passwordInput.text.length)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

                val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale("id", "ID"))
                val formattedDate = dateFormat.format(selectedDate.time)

                binding.etAge.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
}
