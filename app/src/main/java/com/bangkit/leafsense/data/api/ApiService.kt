package com.bangkit.leafsense.data.api

import com.bangkit.leafsense.data.response.ArticlesResponse
import com.bangkit.leafsense.data.response.DetailResponse
import com.bangkit.leafsense.data.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @GET("articles")
    fun getArticles(): Call<ArticlesResponse>

    @GET("articles/{id}")
    suspend fun getArticleDetail(@Path("id") articleId: String): DetailResponse

    @Multipart
    @POST("predict")
    fun predictLeafDisease(
        @Part image: MultipartBody.Part
    ): Call<PredictResponse>

}
