package com.mindorks.bootcamp.instagram.ui.home

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.repository.PostRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Resource
import com.mindorks.bootcamp.instagram.utils.log.Logger
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    userRepository: UserRepository,
    private val postRepository: PostRepository,
    private val allPostList: ArrayList<Post>,
    private val paginator: PublishProcessor<Pair<String?, String?>>
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val posts: MutableLiveData<Resource<List<Post>>> = MutableLiveData()
    val newPostAdded: MutableLiveData<Resource<List<Post>>> = MutableLiveData()
    val fullPostList: MutableLiveData<Resource<List<Post>>> = MutableLiveData()
    val postDeleted: MutableLiveData<Resource<List<Post>>> = MutableLiveData()

    var firstId: String? = null
    var lastId: String? = null

    private val user: User =
        userRepository.getCurrentUser()!! // should not be used without logged in user

    init {
        Logger.d("HomeViewModel", "init")
        compositeDisposable.add(
            paginator
                .onBackpressureDrop()
                .doOnNext {
                    loading.postValue(true)
                    Logger.d("HomeViewModel", "doOnNext")
                }
                .concatMapSingle { pageIds ->
                    Logger.d("HomeViewModel", "pageId = ${pageIds.first} and ${pageIds.second} ")
                    return@concatMapSingle postRepository
                        .fetchHomePostList(pageIds.first, pageIds.second, user)
                        .subscribeOn(schedulerProvider.io())
                        .doOnError {
                            loading.postValue(false)
                            handleNetworkError(it)
                        }
                }
                .subscribe(
                    {
                        Logger.d("HomeViewModel", "old allPostList.size = ${allPostList.size} ")
                        allPostList.addAll(it)
                        Logger.d("HomeViewModel", "new allPostList.size = ${allPostList.size} ")
                        firstId = allPostList.maxByOrNull { post -> post.createdAt.time }?.id
                        lastId = allPostList.minByOrNull { post -> post.createdAt.time }?.id

                        loading.postValue(false)
                        fullPostList.postValue(Resource.success(allPostList))
                        //posts.postValue(Resource.success(it))
                    },
                    {
                        loading.postValue(false)
                        handleNetworkError(it)
                    }
                )
        )
    }

    override fun onCreate() {
        Logger.d("HomeViewModel", "onCreate")
        if (firstId == null && lastId == null) {
            loadMorePosts()
        }
    }

    private fun loadMorePosts() {
        if (checkInternetConnectionWithMessage()) {
            Logger.d("HomeViewModel", "loadMorePosts Pair(firstId, lastId) = ($firstId, $lastId)")
            paginator.onNext(Pair(firstId, lastId))
        }
    }

    fun onLoadMore() {
        if (loading.value !== null && loading.value == false) {
            Logger.d("HomeViewModel", "onLoadMore")
            loadMorePosts()
        }
    }

    fun onNewPost(post: Post) {
        allPostList.add(0, post)
        newPostAdded.postValue(
            Resource.success(
                mutableListOf<Post>().apply {
                    addAll(allPostList)
                }
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        Logger.d("HomeViewModel", "onCleared")
    }

    fun onDeletePost(postId: String) {
        allPostList.let {
            it.find { data -> data.id == postId }.run { allPostList.remove(this) }
        }
        postDeleted.postValue(Resource.success(mutableListOf<Post>().apply {
            addAll(
                allPostList
            )
        }))
    }
}