package com.mindorks.bootcamp.instagram.di_hilt.di.module

//import com.mindorks.paracamera.Camera
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di_hilt.di.*
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.discardchanges.DiscardChangeDialogFragment
import com.mindorks.bootcamp.instagram.ui.imageselection.ImageSelectionDialogFragment
import com.mindorks.bootcamp.instagram.ui.progressdialog.ProgressDialogFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
object ActivityModule {

    @Provides
    fun provideLinearLayoutManager(@ActivityContext activity: BaseActivity<*>): LinearLayoutManager =
        LinearLayoutManager(activity)

//    @Provides
//    fun provideCamera() = Camera.Builder()
//        .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
//        .setTakePhotoRequestCode(1)
//        .setDirectory("temp")
//        .setName("camera_temp_img")
//        .setImageFormat(Camera.IMAGE_JPEG)
//        .setCompression(75)
//        .setImageHeight(500)// it will try to achieve this height as close as possible maintaining the aspect ratio;
//        .build(activity)

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
}