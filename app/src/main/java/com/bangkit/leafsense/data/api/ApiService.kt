package com.bangkit.leafsense.data.api

import com.bangkit.leafsense.data.response.ArticlesResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("articles")
    fun getArticles(): Call<ArticlesResponse>
}
