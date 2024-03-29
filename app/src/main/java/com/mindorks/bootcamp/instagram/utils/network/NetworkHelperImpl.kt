package com.mindorks.bootcamp.instagram.utils.network

import android.content.Context
import android.net.ConnectivityManager
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import com.mindorks.bootcamp.instagram.utils.log.Logger
import java.io.IOException
import java.net.ConnectException

class NetworkHelperImpl constructor(private val context: Context) : NetworkHelper {

    companion object {
        private const val TAG = "NetworkHelper"
    }

    override fun isNetworkConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        return activeNetwork?.isConnected ?: false
    }

    override fun castToNetworkError(throwable: Throwable): NetworkError {
        val defaultNetworkError = NetworkError()
        /*try {*/
            if (throwable is ConnectException) {
                return NetworkError(0, "0")
                /*Not able to connect, please retry again*/
            }
            if (throwable !is HttpException) {
                return defaultNetworkError
                /*Something wrong happened, please retry again*/
            }
            val error = GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .fromJson(
                    throwable.response().errorBody()?.string(),
                    NetworkError::class.java
                )

            return NetworkError(throwable.code(), error.statusCode, error.message)
        /*} catch (e: IOException) {
            Logger.e(TAG, e.toString())
        } catch (e: JsonSyntaxException) {
            Logger.e(TAG, e.toString())
        } catch (e: NullPointerException) {
            Logger.e(TAG, e.toString())
        }*/
        return defaultNetworkError
    }
}