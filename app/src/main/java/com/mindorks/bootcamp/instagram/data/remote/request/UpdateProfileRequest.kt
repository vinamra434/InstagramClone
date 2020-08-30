package com.mindorks.bootcamp.instagram.data.remote.request


import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("profilePicUrl")
    val profilePicUrl: String? = null,
    @SerializedName("tagline")
    val tagline: String? = null
)