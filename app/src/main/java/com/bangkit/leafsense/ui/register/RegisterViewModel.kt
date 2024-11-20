package com.bangkit.leafsense.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.leafsense.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch

class RegisterViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    fun register(name: String, email: String, password: String) {
        _registerResult.value = Result.Loading

        viewModelScope.launch {
            try {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = firebaseAuth.currentUser
                            val profileUpdates = UserProfileChangeRequest.Builder()
                                .setDisplayName(name)
                                .build()

                            firebaseUser?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        _registerResult.value = Result.Success("Registration successful")
                                    } else {
                                        _registerResult.value = Result.Error("Failed to update profile")
                                    }
                                }
                        } else {
                            _registerResult.value = Result.Error(task.exception?.message ?: "Registration failed")
                        }
                    }
            } catch (e: Exception) {
                _registerResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }
}
