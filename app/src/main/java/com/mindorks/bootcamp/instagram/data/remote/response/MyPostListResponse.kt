package com.mindorks.bootcamp.instagram.data.remote.response


import com.google.gson.annotations.SerializedName
import com.mindorks.bootcamp.instagram.data.model.MyPost

data class MyPostListResponse(
    @SerializedName("data")
    val `data`: List<MyPost>,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("statusCode")
    val statusCode: String
)