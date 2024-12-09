package com.bangkit.leafsense.data.response

import com.google.gson.annotations.SerializedName

data class PredictResponse(

    @field:SerializedName("data")
    val data: DataPredict? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("status")
    val status: String? = null
)

data class DataPredict(

    @field:SerializedName("result")
    val result: String? = null,

    @field:SerializedName("probability")
    val probability: Any? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: String? = null,

    @field:SerializedName("cure")
    val cure: String? = null,

    @field:SerializedName("prevention")
    val prevention: String? = null
)
