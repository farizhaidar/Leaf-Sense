package com.bangkit.leafsense.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.leafsense.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            if (user != null) {
                                _loginResult.value = Result.Success("Login successful")
                            } else {
                                _loginResult.value = Result.Error("User not found")
                            }
                        } else {
                            _loginResult.value = Result.Error(task.exception?.message ?: "Login failed")
                        }
                    }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }
}
