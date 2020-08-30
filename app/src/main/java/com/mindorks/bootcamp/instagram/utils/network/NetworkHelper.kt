package com.mindorks.bootcamp.instagram.utils.network

/*for faking network helper we create this interface*/
interface NetworkHelper {

    fun isNetworkConnected(): Boolean

    fun castToNetworkError(throwable: Throwable): NetworkError

}