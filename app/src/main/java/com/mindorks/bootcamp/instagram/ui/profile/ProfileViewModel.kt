package com.mindorks.bootcamp.instagram.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.MyPost
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.log.Logger
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable


class ProfileViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val myPostList: ArrayList<MyPost>
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private var user: User = userRepository.getCurrentUser()!!

    private val userInfo: MutableLiveData<User> = MutableLiveData()

    val launchPostDetail = MutableLiveData<Event<MyPost>>()

    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )
    val loading: MutableLiveData<Boolean> = MutableLiveData()

    val refreshDeletePost: MutableLiveData<Resource<List<MyPost>>> = MutableLiveData()

    val myPosts: MutableLiveData<Resource<List<MyPost>>> = MutableLiveData()

    val refreshPosts: MutableLiveData<Resource<List<MyPost>>> = MutableLiveData()

    val userName: LiveData<String> = Transformations.map(userInfo) {
        it.name
    }

    val launchEditProfile: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    val logout: MutableLiveData<Event<Map<String, String>>> = MutableLiveData()

    val tagLine: LiveData<String> = Transformations.map(userInfo) {
        it.tagline
    }
    val profileImage: LiveData<Image> = Transformations.map(userInfo) {
        it.profilePicUrl?.run {
            Image(this, headers)
        }
    }

    init {
        loading.postValue(true)
        compositeDisposable.add(
            userRepository
                .fetchMyPostList(user)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        if (it != null) {
                            myPostList.addAll(it)
                            myPosts.postValue(Resource.success(it))
                        } else {
                            myPosts.postValue(Resource.error())
                        }
                        loading.postValue(false)

                    },
                    {
                        loading.postValue(false)
                        handleNetworkError(it)
                    }
                )
        )

    }

    override fun onCreate() {
        Logger.d("ProfileViewModel", "onCreate")
        compositeDisposable.add(
            userRepository
                .fetchUserInfo(user)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        userInfo.postValue(it)
                        userRepository.saveCurrentUser(it)
                    },
                    {
                        it.message?.let { it1 -> Logger.d("error", it1) }
                        handleNetworkError(it)
                    }
                )
        )
    }

    fun onEditClicked() {
        launchEditProfile.postValue(Event(emptyMap()))
    }

    fun onLogout() {
        compositeDisposable.add(
            userRepository.doUserLogout(user).map {
                if (it)
                    userRepository.removeCurrentUser()
            }
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    {
                        logout.postValue(Event(emptyMap()))
                    },
                    {
                        it.message?.let { it1 -> Logger.d("error", it1) }
                        handleNetworkError(it)
                    }
                )
        )

    }

    fun onPostImageClicked(adapterPosition: Int) {
        launchPostDetail.postValue(Event(myPostList[adapterPosition]))
    }

    fun onDeletePost(postId: String) {
        myPostList.let {
            it.find { data -> data.id == postId }.run { myPostList.remove(this) }
        }
        refreshDeletePost.postValue(Resource.success(mutableListOf<MyPost>().apply {
            addAll(
                myPostList
            )
        }))
    }

    fun onNewPost(myPost: MyPost) {
        myPostList.add(0, myPost)
        refreshPosts.postValue(Resource.success(mutableListOf<MyPost>()
            .apply { addAll(myPostList) }))
    }

    override fun onCleared() {
        super.onCleared()
        Logger.d("ProfileViewModel","onCleared")
    }


}