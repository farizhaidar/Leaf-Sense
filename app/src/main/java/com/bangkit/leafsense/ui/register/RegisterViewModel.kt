package com.bangkit.leafsense.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.leafsense.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class RegisterViewModel(private val firebaseAuth: FirebaseAuth) : ViewModel() {

    private val _registerResult = MutableLiveData<Result<String>>()
    val registerResult: LiveData<Result<String>> = _registerResult

    private val firestore = FirebaseFirestore.getInstance()

    fun register(name: String, email: String, password: String, age: String, job: String) {
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
                                        firebaseUser.sendEmailVerification()
                                            .addOnCompleteListener { emailTask ->
                                                if (emailTask.isSuccessful) {
                                                    saveUserToFirestore(firebaseUser.uid, name, email, age, job)
                                                } else {
                                                    _registerResult.value = Result.Error("Failed to send verification email")
                                                }
                                            }
                                    } else {
                                        _registerResult.value = Result.Error("Failed to update profile")
                                    }
                                }
                        } else {
                            _registerResult.value = Result.Error(task.exception?.message ?: "Registration failed")
                        }
                    }
                    .addOnFailureListener { exception ->
                        _registerResult.value = Result.Error(exception.message ?: "An error occurred during registration")
                    }
            } catch (e: Exception) {
                _registerResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }


    private fun saveUserToFirestore(userId: String?, name: String, email: String, age: String, job: String) {
        val user = hashMapOf(
            "name" to name,
            "email" to email,
            "age" to age,
            "job" to job
        )

        userId?.let {
            firestore.collection("users").document(it).set(user)
                .addOnSuccessListener {
                    _registerResult.value = Result.Success("Registration successful")
                }
                .addOnFailureListener { e ->
                    _registerResult.value = Result.Error("Failed to save user data: ${e.message}")
                }
        }
    }
}
