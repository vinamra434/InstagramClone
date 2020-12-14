package com.mindorks.bootcamp.instagram.di.module

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.repository.PhotoRepository
import com.mindorks.bootcamp.instagram.data.repository.UserRepository
import com.mindorks.bootcamp.instagram.di.*
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.discardchanges.DiscardChangeDialogFragment
import com.mindorks.bootcamp.instagram.ui.discardchanges.DiscardChangeViewModel
import com.mindorks.bootcamp.instagram.ui.editprofile.EditProfileViewModel
import com.mindorks.bootcamp.instagram.ui.imageselection.ImageSelectionDialogFragment
import com.mindorks.bootcamp.instagram.ui.imageselection.ImageSelectionViewModel
import com.mindorks.bootcamp.instagram.ui.login.LoginViewModel
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.ui.main.MainViewModel
import com.mindorks.bootcamp.instagram.ui.progressdialog.ProgressDialogFragment
import com.mindorks.bootcamp.instagram.ui.signup.SignUpViewModel
import com.mindorks.bootcamp.instagram.ui.splash.SplashViewModel
import com.mindorks.bootcamp.instagram.utils.ViewModelProviderFactory
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import com.mindorks.paracamera.Camera
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import java.io.File

@Module
class ActivityModule(private val activity: BaseActivity<*>) {

    @Provides
    fun provideLinearLayoutManager(): LinearLayoutManager = LinearLayoutManager(activity)

    @Provides
    fun provideSplashViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): SplashViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(SplashViewModel::class) {
            SplashViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
            //this lambda creates and return SplashViewModel
        }).get(SplashViewModel::class.java)

    @Provides
    fun provideLoginViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): LoginViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(LoginViewModel::class) {
            LoginViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
        }).get(LoginViewModel::class.java)

    @Provides
    fun provideSignUpViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository
    ): SignUpViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(SignUpViewModel::class) {
            SignUpViewModel(schedulerProvider, compositeDisposable, networkHelper, userRepository)
        }).get(SignUpViewModel::class.java)

    @Provides
    fun provideEditProfileViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
        userRepository: UserRepository,
        photoRepository: PhotoRepository,
        @TempDirectory directory: File
    ): EditProfileViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(EditProfileViewModel::class) {
            EditProfileViewModel(
                schedulerProvider,
                compositeDisposable,
                networkHelper,
                userRepository,
                photoRepository,
                directory
            )
        }).get(EditProfileViewModel::class.java)

    @Provides
    fun provideMainViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(MainViewModel::class) {
            MainViewModel(schedulerProvider, compositeDisposable, networkHelper)
        }).get(MainViewModel::class.java)

    @Provides
    fun provideMainSharedViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper
    ): MainSharedViewModel = ViewModelProviders.of(
        activity, ViewModelProviderFactory(MainSharedViewModel::class) {
            MainSharedViewModel(schedulerProvider, compositeDisposable, networkHelper)
        }).get(MainSharedViewModel::class.java)

    @Provides
    fun provideCamera() = Camera.Builder()
        .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
        .setTakePhotoRequestCode(1)
        .setDirectory("temp")
        .setName("camera_temp_img")
        .setImageFormat(Camera.IMAGE_JPEG)
        .setCompression(75)
        .setImageHeight(500)// it will try to achieve this height as close as possible maintaining the aspect ratio;
        .build(activity)

    @Provides
    @SavingProfileDialog
    fun provideSavingProfileDialog(): ProgressDialogFragment =
        ProgressDialogFragment.newInstance(R.string.saving_profile_message)

    @Provides
    @UploadImageDialog
    fun provideUploadImageProgressDialog(): ProgressDialogFragment =
        ProgressDialogFragment.newInstance(R.string.upload_image_message)

    @Provides
    @ImageSelectionDialog
    fun provideImageSelectionProgressDialog(): ProgressDialogFragment =
        ProgressDialogFragment.newInstance(R.string.upload_image_message)

    @Provides
    fun provideDiscardChangeDialog(): DiscardChangeDialogFragment =
        DiscardChangeDialogFragment.newInstance()

    @Provides
    fun provideImageSelectionDialog(): ImageSelectionDialogFragment =
        ImageSelectionDialogFragment.newInstance()

    @Provides
    fun provideDiscardChangeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
    ): DiscardChangeViewModel = ViewModelProvider(activity, ViewModelProviderFactory(
        DiscardChangeViewModel::class
    ) {
        DiscardChangeViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(DiscardChangeViewModel::class.java)

    @Provides
    fun provideImageSelectionViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
    ): ImageSelectionViewModel = ViewModelProvider(activity, ViewModelProviderFactory(
        ImageSelectionViewModel::class
    ) {
        ImageSelectionViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(ImageSelectionViewModel::class.java)
}