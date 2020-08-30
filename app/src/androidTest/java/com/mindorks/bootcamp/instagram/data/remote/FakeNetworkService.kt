package com.mindorks.bootcamp.instagram.data.remote

import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.remote.request.DummyRequest
import com.mindorks.bootcamp.instagram.data.remote.request.LoginRequest
import com.mindorks.bootcamp.instagram.data.remote.request.PostCreationRequest
import com.mindorks.bootcamp.instagram.data.remote.request.PostLikeModifyRequest
import com.mindorks.bootcamp.instagram.data.remote.response.*
import io.reactivex.Single
import okhttp3.MultipartBody
import java.util.*

class  FakeNetworkService : NetworkService {

    override fun doLoginCall(request: LoginRequest, apiKey: String): Single<LoginResponse> {
        return Single.just(
            LoginResponse(
                "statusCode",
                200,
                "success",
                "accessToken",
                "userId",
                "userName",
                "userEmail",
                "profilePicUrl"
            )
        )
    }

    override fun doHomePostListCall(
        firstPostId: String?,
        lastPostId: String?,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostListResponse> {

        val creator1 = Post.User("userId1", "name1", "profilePicUrl1")
        val creator2 = Post.User("userId2", "name2", "profilePicUrl2")

        val likedBy = mutableListOf<Post.User>(
            Post.User("userId3", "name3", "profilePicUrl3"),
            Post.User("userId4", "name4", "profilePicUrl4")
        )

        val post1 = Post("postId1", "imgUrl1", 400, 400, creator1, likedBy, Date())
        val post2 = Post("postId2", "imgUrl2", 400, 400, creator2, likedBy, Date())

        val postListResponse = PostListResponse("statusCode", "success", listOf(post1, post2))

        return Single.just(postListResponse)
    }

    override fun doPostLikeCall(
        request: PostLikeModifyRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doPostUnlikeCall(
        request: PostLikeModifyRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<GeneralResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doImageUpload(
        image: MultipartBody.Part,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<ImageResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doPostCreationCall(
        request: PostCreationRequest,
        userId: String,
        accessToken: String,
        apiKey: String
    ): Single<PostCreationResponse> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}