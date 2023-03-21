package com.mindorks.bootcamp.instagram.data.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class MyPost(
    @SerializedName("createdAt")
    val createdAtsss: Date,
    @SerializedName("id")
    val id: String,
    @SerializedName("imgHeight")
    val imgHeight: Int?,
    @SerializedName("imgUrl")
    val imgUrl: String,
    @SerializedName("imgWidth")
    val imgWidth: Int?
)