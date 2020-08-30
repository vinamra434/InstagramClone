package com.mindorks.bootcamp.instagram.data.remote.response


import com.google.gson.annotations.SerializedName

data class ProfileInfoResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: Int,
    @SerializedName("statusCode")
    val statusCode: String
) {
    data class Data(
        @SerializedName("id")
        val id: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("profilePicUrl")
        val profilePicUrl: String,
        @SerializedName("tagline")
        val tagline: String
    )
}