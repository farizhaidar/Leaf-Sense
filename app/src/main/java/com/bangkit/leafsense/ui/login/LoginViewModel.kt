package com.bangkit.leafsense.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.leafsense.AuthRepository
import com.bangkit.leafsense.Result
import com.bangkit.leafsense.data.response.LoginResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginResult.value = Result.Loading
            try {
                val response: Response<LoginResponse> = authRepository.login(email, password)
                if (response.isSuccessful) {
                    val token = response.body()?.loginResult?.token
                    if (!token.isNullOrEmpty()) {
                        _loginResult.value = Result.Success(token)
                    } else {
                        _loginResult.value = Result.Error("Token is empty")
                    }
                } else {
                    _loginResult.value = Result.Error(response.message() ?: "Login error")
                }
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "An error occurred")
            }
        }
    }
}
