package com.mindorks.bootcamp.instagram.ui.editprofile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EditProfileBroadcast @Inject constructor(userRepository: UserRepository) {

    private var user = userRepository.getCurrentUser()!!

    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )

    val userInfo: MutableLiveData<User> = MutableLiveData()

    val userName: LiveData<String> = Transformations.map(userInfo) {
        it.name
    }

    val tagLine: LiveData<String> = Transformations.map(userInfo) {
        it.tagline
    }
    val profileImage: LiveData<Image> = Transformations.map(userInfo) {
        it.profilePicUrl?.run {
            Image(this, headers)
        }
    }
}