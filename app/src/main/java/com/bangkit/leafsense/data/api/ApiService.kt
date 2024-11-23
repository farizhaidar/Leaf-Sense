package com.bangkit.leafsense.data.api

import com.bangkit.leafsense.data.response.ArticlesResponse
import com.bangkit.leafsense.data.response.DetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("articles")
    fun getArticles(): Call<ArticlesResponse>

    @GET("articles/{id}")
    suspend fun getArticleDetail(@Path("id") articleId: String): DetailResponse

}
