package com.bangkit.leafsense.ui

import com.bangkit.leafsense.data.api.ApiService
import com.bangkit.leafsense.data.response.DetailResponse

class ArticleRepository(private val apiService: ApiService) {

    suspend fun getArticleDetail(articleId: String): DetailResponse {
        return apiService.getArticleDetail(articleId)
    }
}
