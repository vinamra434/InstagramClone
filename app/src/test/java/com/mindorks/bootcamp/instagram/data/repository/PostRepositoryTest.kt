package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.data.remote.response.PostListResponse
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class PostRepositoryTest {

    @Mock
    private lateinit var networkService: NetworkService

    private lateinit var postRepository: PostRepository

    @Before
    fun setUp() {
        Networking.API_KEY = "FAKE_API_KEY"
        postRepository = PostRepository(networkService)
    }

    @Test
    fun fetchHomePostList_requestDoHomePostListCall() {

        val user = User("userId", "userName", "userEmail", "accessToken", "profilePicUrl")

        /*when networkService is used return Single */
        doReturn(Single.just(PostListResponse("statusCode", "message", listOf())))
            .`when`(networkService)
            .doHomePostListCall(
                "firstPostI",
                "lastPostId",
                user.id,
                user.accessToken,
                Networking.API_KEY
            )

        postRepository.fetchHomePostList("firstPostI", "lastPostId", user)

        verify(networkService).doHomePostListCall(
            "firstPostI",
            "lastPostId",
            user.id,
            user.accessToken,
            Networking.API_KEY
        )

    }

}