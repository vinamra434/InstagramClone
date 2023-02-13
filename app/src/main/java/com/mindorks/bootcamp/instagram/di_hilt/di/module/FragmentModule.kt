package com.mindorks.bootcamp.instagram.di_hilt.di.module

//import com.mindorks.paracamera.Camera
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.MyPost
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.di_hilt.di.UploadImageDialog
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.home.posts.PostsAdapter
import com.mindorks.bootcamp.instagram.ui.profile.ProfileFragment
import com.mindorks.bootcamp.instagram.ui.profile.myposts.MyPostsAdapter
import com.mindorks.bootcamp.instagram.ui.progressdialog.ProgressDialogFragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent

@InstallIn(FragmentComponent::class)
@Module
object FragmentModule {

    //private val fragment: BaseFragment<*>

    @Provides
    fun provideLinearLayoutManager(fragment: BaseFragment<*>): LinearLayoutManager = LinearLayoutManager(fragment.context)

    @Provides
    fun providePostsAdapter(fragment: BaseFragment<*>) = PostsAdapter(fragment.lifecycle, ArrayList())

    @Provides
    fun provideMyPostsAdapter(fragment: BaseFragment<*>) =
        MyPostsAdapter(fragment.lifecycle, ArrayList(), fragment as ProfileFragment)

//    @Provides
//    fun provideCamera() = Camera.Builder()
//        .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
//        .setTakePhotoRequestCode(1)
//        .setDirectory("temp")
//        .setName("camera_temp_img")
//        .setImageFormat(Camera.IMAGE_JPEG)
//        .setCompression(75)
//        .setImageHeight(500)// it will try to achieve this height as close as possible maintaining the aspect ratio;
//        .build(fragment)

    @Provides
    @UploadImageDialog
    fun provideUploadImageDialog(): ProgressDialogFragment =
        ProgressDialogFragment.newInstance(R.string.upload_image_message)

    @Provides
    fun provideEmptyMyPosts() = arrayListOf<MyPost>()

    @Provides
    fun provideEmptyPosts() = arrayListOf<Post>()

}