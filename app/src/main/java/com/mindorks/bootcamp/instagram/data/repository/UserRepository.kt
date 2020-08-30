package com.mindorks.bootcamp.instagram.data.repository

import com.mindorks.bootcamp.instagram.data.local.db.DatabaseService
import com.mindorks.bootcamp.instagram.data.local.prefs.UserPreferences
import com.mindorks.bootcamp.instagram.data.model.MyPost
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.data.model.User
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.request.LoginRequest
import com.mindorks.bootcamp.instagram.data.remote.request.SignUpRequest
import com.mindorks.bootcamp.instagram.data.remote.request.UpdateProfileRequest
import com.mindorks.bootcamp.instagram.data.remote.response.GeneralResponse
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val networkService: NetworkService,
    private val userPreferences: UserPreferences
) {

    fun saveCurrentUser(user: User) {
        userPreferences.setUserId(user.id)
        userPreferences.setUserName(user.name)
        userPreferences.setUserEmail(user.email)
        userPreferences.setAccessToken(user.accessToken)
        user.profilePicUrl?.let { userPreferences.setProfileUrl(it) }
        user.tagline?.let { userPreferences.setUserBio(it) }
    }


    fun removeCurrentUser() {
        userPreferences.removeUserId()
        userPreferences.removeUserName()
        userPreferences.removeUserEmail()
        userPreferences.removeAccessToken()
        userPreferences.removeProfilePic()
        userPreferences.removeUserBio()
    }

    fun getCurrentUser(): User? {

        val userId = userPreferences.getUserId()
        val userName = userPreferences.getUserName()
        val userEmail = userPreferences.getUserEmail()
        val accessToken = userPreferences.getAccessToken()
        val profilePicUrl = userPreferences.getProfileUrl()
        val userBio = userPreferences.getUserBio()


        return if (userId !== null && userName != null && userEmail != null && accessToken != null)
            User(userId, userName, userEmail, accessToken, profilePicUrl, userBio)
        else
            null
    }

    /*todo test login with fake wrong detail*/
    fun doUserLogin(email: String, password: String): Single<User> =
        networkService.doLoginCall(LoginRequest(email, password))
            .map {
                User(
                    it.userId,
                    it.userName,
                    it.userEmail,
                    it.accessToken,
                    it.profilePicUrl
                )
            }

    fun doUserSignUp(email: String, password: String, fullName: String): Single<User> =
        networkService.doSignUpCall(SignUpRequest(email, password, fullName))
            .map {
                User(
                    it.userId,
                    it.userName,
                    it.userEmail,
                    it.accessToken
                )
            }

    fun fetchUserInfo(user: User): Single<User> {
        return networkService.doFetchUserInfoCall(user.id, user.accessToken)
            .map {
                User(
                    id = it.data.id,
                    name = it.data.name,
                    profilePicUrl = it.data.profilePicUrl,
                    tagline = it.data.tagline,
                    accessToken = user.accessToken,
                    email = user.email
                )
            }
    }

    fun fetchMyPostList(
        user: User
    ): Single<List<MyPost>?> {
        return networkService.doUserPostListCall(
            user.id,
            user.accessToken
        ).map { if (it.status == 200) it.data else null }
    }

    fun updateUserProfile(user: User, request: UpdateProfileRequest): Single<GeneralResponse> {
        return networkService.updateUserInfo(user.id, user.accessToken, request = request)
    }

    fun doUserLogout(user: User): Single<Boolean> {
        return networkService.doUserLogoutCall(user.id, user.accessToken)
            .map {
                return@map it.status == 200
            }
    }

    fun doUserDeletePost(user: User, postId: String): Single<Boolean> {
        return networkService.doDeletePostCall(postId, user.id, user.accessToken)
            .map {
                it.status == 200
            }
    }

    fun doUserPostDetailCall(user: User, postId: String): Single<Post?> {
        return networkService.doPostDetailCall(postId, user.id, user.accessToken).map {
            if (it.status == 200) it.data else null
        }
    }

}