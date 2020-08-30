package com.mindorks.bootcamp.instagram.ui.postdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Image
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.common.TimeUtils
import com.mindorks.bootcamp.instagram.utils.display.ScreenUtils
import com.mindorks.bootcamp.instagram.utils.log.Logger
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class PostDetailViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    private val user = userRepository.getCurrentUser()!!
    private val screenWidth = ScreenUtils.getScreenWidth()
    private val screenHeight = ScreenUtils.getScreenHeight()
    private val headers = mapOf(
        Pair(Networking.HEADER_API_KEY, Networking.API_KEY),
        Pair(Networking.HEADER_USER_ID, user.id),
        Pair(Networking.HEADER_ACCESS_TOKEN, user.accessToken)
    )


    private val post = MutableLiveData<Post>()
    val loading = MutableLiveData<Boolean>()
    val imageDeleted = MutableLiveData<Event<String>>()
    val sendPostLike = MutableLiveData<Post>()

    val name: LiveData<String> = Transformations.map(post) {
        Logger.d("PostDetailViewModel", "name")
        it.creator.name
    }
    val postTime: LiveData<String> =
        Transformations.map(post) { Logger.d("PostDetailViewModel", "postTime")
            TimeUtils.getTimeAgo(it.createdAt) }
    val likesCount: LiveData<Int> = Transformations.map(post) {
        Logger.d("PostDetailViewModel", "likesCount")
        it.likedBy?.size ?: 0 }
    val isLiked: LiveData<Boolean> = Transformations.map(post) {
        Logger.d("PostDetailViewModel", "isLiked")
        it.likedBy?.find { postUser -> postUser.id == user.id } !== null
    }

    val profileImage: LiveData<Image> = Transformations.map(post) {
        it.creator.profilePicUrl?.run { Image(this, headers) }
    }

    val imageDetail: LiveData<Image> = Transformations.map(post) {
        Image(
            it.imageUrl,
            headers,
            screenWidth,
            it.imageHeight?.let { height ->
                return@let (calculateScaleFactor(it) * height).toInt()
            } ?: screenHeight / 3)
    }

    fun getPostDetail(postId: String) {
        Logger.d("PostDetailViewModel", "getPostDetail")
        loading.postValue(true)
        compositeDisposable.addAll(
            userRepository.doUserPostDetailCall(user, postId)
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    { postDetail ->
                        loading.postValue(false)
                        if (postDetail != null) {
                            Logger.d("PostDetailViewModel", "posting value")
                            post.postValue(postDetail)
                        }
                    },
                    {
                        handleNetworkError(it)
                        loading.postValue(false)
                    }
                )
        )
    }

    private fun calculateScaleFactor(post: Post) =
        post.imageWidth?.let { return@let screenWidth.toFloat() / it } ?: 1f

    override fun onCreate() {
    }

    fun onDeletePost(postId: String?) {
        Logger.d("PostDetailViewModel", "delete post with id = $postId")
        postId?.let {
            loading.postValue(true)
            compositeDisposable.addAll(
                userRepository.doUserDeletePost(user, it)
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        { isDeleted ->
                            loading.postValue(false)
                            if (isDeleted) {
                                imageDeleted.postValue(Event(postId))
                            }
                        },
                        {
                            handleNetworkError(it)
                            loading.postValue(false)
                        }
                    )
            )
        }
    }

    fun onLikeClick() = post.value?.let {
        if (networkHelper.isNetworkConnected()) {
            val api =
                if (isLiked.value == true)
                    postRepository.makeUnlikePost(it, user)
                else
                    postRepository.makeLikePost(it, user)

            compositeDisposable.add(api
                .subscribeOn(schedulerProvider.io())
                .subscribe(
                    { responsePost ->
                        if (responsePost.id == it.id) {
                            post.postValue(responsePost)
                            sendPostLike.postValue(responsePost)
                        }
                    },
                    { error -> handleNetworkError(error) }
                )
            )
        } else {
            messageStringId.postValue(Resource.error(R.string.network_connection_error))
        }
    }

    override fun onCleared() {
        super.onCleared()
        Logger.d("PostDetailViewModel", "onCleared" )
    }
}