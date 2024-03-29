package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class PhotoRepository @Inject constructor(private val networkService: NetworkService) {

    fun uploadPhoto(file: File, user: User): Single<String> {
        return MultipartBody.Part.createFormData(
            "image", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
        ).run {
            return@run networkService.doImageUpload(this, user.id, user.accessToken)
                .map { it.data.imageUrl }
        }
    }
}