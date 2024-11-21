package com.bangkit.leafsense.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bangkit.leafsense.R
import com.bangkit.leafsense.Result
import com.bangkit.leafsense.databinding.ActivityLoginBinding
import com.bangkit.leafsense.ui.MainActivity
import com.bangkit.leafsense.ui.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory(FirebaseAuth.getInstance())
    }

    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (isLoggedIn()) {
            navigateToMainActivity()
        }

        observeLoginResult()

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email dan password harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show Lottie animation while logging in
            binding.loadingAnimation.visibility = View.VISIBLE

            loginViewModel.login(email, password)
        }

        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Password visibility toggle logic
        val passwordEditText = binding.etPassword
        val eyeIcon = binding.showPasswordButton

        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            togglePasswordVisibility(isPasswordVisible, passwordEditText, eyeIcon)
        }
    }

    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // The Lottie animation is already shown in btnLogin click
                }
                is Result.Success -> {
                    binding.loadingAnimation.visibility = View.GONE
                    Toast.makeText(this, result.data, Toast.LENGTH_SHORT).show()
                    saveLoginStatus(true)
                    navigateToMainActivity()
                }
                is Result.Error -> {
                    binding.loadingAnimation.visibility = View.GONE
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
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
