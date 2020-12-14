package com.mindorks.bootcamp.instagram.ui.photo

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.UploadImageDialog
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.ui.progressdialog.ProgressDialogFragment
import com.mindorks.bootcamp.instagram.utils.log.Logger
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.fragment_photo.*
import java.io.FileNotFoundException
import javax.inject.Inject


class PhotoFragment : BaseFragment<PhotoViewModel>() {

    companion object {

        const val TAG = "PhotoFragment"
        const val RESULT_GALLERY_IMG = 1001

        fun newInstance(): PhotoFragment {
            val args = Bundle()
            val fragment = PhotoFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    @Inject
    @UploadImageDialog
    lateinit var uploadImageDialog: ProgressDialogFragment

    @Inject
    lateinit var camera: Camera

    override fun provideLayoutId(): Int = R.layout.fragment_photo

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.loading.observe(this, Observer {
            pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.uploadImageLoading.observe(this, Observer {
            if (it) showImageUploadLoader() else hideImageUploadDialog()
        })

        viewModel.post.observe(this, Observer {
            it.getIfNotHandled()?.run {
                mainSharedViewModel.newPost.postValue(this)
                mainSharedViewModel.onHomeRedirect()
            }
        })

    }

    private fun hideImageUploadDialog() {
        Logger.d(TAG, "hideImageUploadDialog")
//        uploadPhotoLoading.dismiss()
        uploadImageDialog.dismiss()
    }

    private fun showImageUploadLoader() {
        Logger.d(TAG, "showImageUploadLoader")
        /*val transaction = childFragmentManager.beginTransaction()
        uploadPhotoLoading.show(transaction, DialogSavingDetail.TAG)*/
        uploadImageDialog.show(childFragmentManager, ProgressDialogFragment.TAG)
    }

    override fun setupView(view: View) {
        view_gallery.setOnClickListener {
            Intent(Intent.ACTION_PICK)
                .apply {
                    type = "image/*"
                }.run {
                    startActivityForResult(this, RESULT_GALLERY_IMG)
                }
        }

        view_camera.setOnClickListener {
            try {
                camera.takePicture()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(reqCode, resultCode, intent)
        if (resultCode == RESULT_OK) {
            when (reqCode) {
                RESULT_GALLERY_IMG -> {
                    try {
                        intent?.data?.let {
                            activity?.contentResolver?.openInputStream(it)?.run {
                                viewModel.onGalleryImageSelected(this)
                            }
                        } ?: showMessage(R.string.try_again)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        showMessage(R.string.try_again)
                    }
                }
                Camera.REQUEST_TAKE_PHOTO -> {
                    viewModel.onCameraImageTaken { camera.cameraBitmapPath }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy")
    }

    override fun onStart() {
        super.onStart()
        Logger.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Logger.d(TAG, "onStop")
    }

    override fun onResume() {
        super.onResume()
        Logger.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Logger.d(TAG, "onPause")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate")
    }
}