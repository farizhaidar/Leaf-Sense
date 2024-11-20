//package com.bangkit.leafsense
//
//import com.bangkit.leafsense.data.api.ApiConfig
//import com.bangkit.leafsense.data.api.ApiService
//import com.bangkit.leafsense.data.response.LoginResponse
//import retrofit2.Response
//
//class AuthRepository(private val apiService: ApiService) {
//
//    suspend fun register(name: String, email: String, password: String) {
//        val response: Response<RegisterResponse> = apiService.register(name, email, password)
//
//        if (!response.isSuccessful) {
//            throw Exception("Gagal registrasi: ${response.message()}")
//        }
//    }
//
//    suspend fun login(email: String, password: String): Response<LoginResponse> {
//        return ApiConfig.getApiService().login(email, password)
//    }
//}
