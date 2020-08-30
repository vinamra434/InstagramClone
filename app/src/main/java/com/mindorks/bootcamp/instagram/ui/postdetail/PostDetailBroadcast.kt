package com.mindorks.bootcamp.instagram.ui.postdetail

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.utils.common.Event
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostDetailBroadcast @Inject constructor(userRepository: UserRepository) {

    val postLiked = MutableLiveData<Event<Post>>()

    val postDeleted = MutableLiveData<String>()
}