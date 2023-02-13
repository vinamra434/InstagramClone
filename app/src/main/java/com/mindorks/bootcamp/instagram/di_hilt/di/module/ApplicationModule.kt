package com.mindorks.bootcamp.instagram.di_hilt.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.mindorks.bootcamp.instagram.BuildConfig
import com.mindorks.bootcamp.instagram.InstagramApplication
import com.mindorks.bootcamp.instagram.data.local.db.DatabaseService
import com.mindorks.bootcamp.instagram.data.remote.NetworkService
import com.mindorks.bootcamp.instagram.data.remote.Networking
import com.mindorks.bootcamp.instagram.di_hilt.di.ApplicationContext
import com.mindorks.bootcamp.instagram.di_hilt.di.TempDirectory
import com.mindorks.bootcamp.instagram.utils.common.FileUtils
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelperImpl
import com.mindorks.bootcamp.instagram.utils.rx.RxSchedulerProvider
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Singleton

/*
* parent module of app which provides dependencies throughtout the app*/

@InstallIn(SingletonComponent::class)
@Module
object ApplicationModule {
    @Provides
    @Singleton
    fun provideApplication(@ApplicationContext application: InstagramApplication): Application =
        application

    @Provides
    @Singleton
    @ApplicationContext
    fun provideContext(@ApplicationContext application: InstagramApplication): Context = application

    @Provides
    @Singleton
    @TempDirectory
    fun provideTempDirectory(@ApplicationContext application: InstagramApplication) =
        FileUtils.getDirectory(application, "temp")

    @Provides
    @Singleton
    fun provideSchedulerProvider(): SchedulerProvider = RxSchedulerProvider()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext application: InstagramApplication): SharedPreferences =
        application.getSharedPreferences("instagram-project", Context.MODE_PRIVATE)

    /**
     * We need to write @Singleton on the provide method if we are create the instance inside this method
     * to make it singleton. Even if we have written @Singleton on the instance's class
     */
    @Provides
    @Singleton
    fun provideDatabaseService(@ApplicationContext application: InstagramApplication): DatabaseService =
        Room.databaseBuilder(
            application, DatabaseService::class.java,
            "bootcamp-instagram-project-db"
        ).build()

    @Provides
    @Singleton
    fun provideNetworkService(@ApplicationContext application: InstagramApplication): NetworkService =
        Networking.create(
            BuildConfig.API_KEY,
            BuildConfig.BASE_URL,
            application.cacheDir,
            10 * 1024 * 1024 // 10MB
        )

    @Singleton
    @Provides
    fun provideNetworkHelper(@ApplicationContext application: InstagramApplication): NetworkHelper =
        NetworkHelperImpl(application)

    /**
     * each time new instance is provided
     */
    @Provides
    fun provideCompositeDisposable(): CompositeDisposable = CompositeDisposable()

}