package com.bangkit.leafsense.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.leafsense.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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
                                if (user.isEmailVerified) {
                                    _loginResult.value = Result.Success("Berhasil Masuk")
                                } else {
                                    _loginResult.value = Result.Error("Email tidak terverifikasi. Silakan periksa kotak masuk email Anda")
                                    firebaseAuth.signOut()
                                }
                            } else {
                                _loginResult.value = Result.Error("Pengguna tidak ditemukan")
                            }
                        } else {
                            _loginResult.value = Result.Error(task.exception?.message ?: "Gagal Masuk")
                        }
                    }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }

}
