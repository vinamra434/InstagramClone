package com.mindorks.bootcamp.instagram

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*here we keep application wide singletons*/
@HiltAndroidApp
class InstagramApplication : Application() {

    override fun onCreate() {
        super.onCreate()
    }

}