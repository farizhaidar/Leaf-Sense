package com.bangkit.leafsense.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bangkit.leafsense.AuthRepository
import com.bangkit.leafsense.Result
import kotlinx.coroutines.Dispatchers

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun register(name: String, email: String, password: String): LiveData<Result<Unit>> = liveData(
        Dispatchers.IO
    ) {
        emit(Result.loading())
        try {
            authRepository.register(name, email, password)
            emit(Result.success(Unit))
        } catch (e: Exception) {
            emit(Result.error(e.message ?: "Terjadi kesalahan"))
        }
    }
}
