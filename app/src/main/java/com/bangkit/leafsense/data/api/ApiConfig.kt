package com.bangkit.leafsense.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        private fun createRetrofit(baseUrl: String): Retrofit {
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        fun getApiService(): ApiService {
            val retrofit = createRetrofit("https://leaf-sense-api-7l3mig7ida-et.a.run.app/")
            return retrofit.create(ApiService::class.java)
        }

        fun getClassificationApiService(): ApiService {
            val retrofit = createRetrofit("https://leaf-sense-ml-api-716993595320.asia-southeast2.run.app/")
            return retrofit.create(ApiService::class.java)
        }
    }
}
