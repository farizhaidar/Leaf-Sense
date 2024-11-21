package com.bangkit.leafsense.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.leafsense.databinding.ActivityRegisterBinding
import com.bangkit.leafsense.ui.login.LoginActivity
import com.bangkit.leafsense.Result
import com.bangkit.leafsense.ui.register.RegisterViewModel
import com.bangkit.leafsense.ui.register.RegisterViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import androidx.lifecycle.Observer
import android.widget.ImageView
import android.widget.EditText
import com.bangkit.leafsense.R

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private val registerViewModel: RegisterViewModel by viewModels { RegisterViewModelFactory(FirebaseAuth.getInstance()) }

    // Define the password visibility state
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
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    navigateToLoginActivity()
                }
                is Result.Error -> {
                    binding.loadingAnimation.visibility = View.GONE
                    Toast.makeText(this, "Registration failed: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(name, email, password)
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGoToLogin.setOnClickListener {
            navigateToLoginActivity()
        }

        // Password visibility toggle logic
        val passwordEditText: EditText = binding.etPassword
        val eyeIcon: ImageView = binding.showPasswordButton

        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(isPasswordVisible, passwordEditText, eyeIcon)
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        registerViewModel.register(name, email, password)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Toggle password visibility
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
}
