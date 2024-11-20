package com.bangkit.leafsense.data.response

import com.google.gson.annotations.SerializedName

data class ArticlesResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataItem(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("content")
	val content: String? = null,

	@field:SerializedName("plantType")
	val plantType: String? = null,

	@field:SerializedName("updatedAt")
	val updatedAt: String? = null
)
