package com.mindorks.bootcamp.instagram.ui.editprofile

//import com.mindorks.paracamera.Camera
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di_hilt.di.SavingProfileDialog
import com.mindorks.bootcamp.instagram.di_hilt.di.UploadImageDialog
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.discardchanges.DiscardChangeDialogFragment
import com.mindorks.bootcamp.instagram.ui.discardchanges.DiscardChangeViewModel
import com.mindorks.bootcamp.instagram.ui.imageselection.ImageSelectionDialogFragment
import com.mindorks.bootcamp.instagram.ui.imageselection.ImageSelectionViewModel
import com.mindorks.bootcamp.instagram.ui.progressdialog.ProgressDialogFragment
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.bootcamp.instagram.utils.common.Status
import com.mindorks.bootcamp.instagram.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.item_view_post.view.*
import java.io.FileNotFoundException
import javax.inject.Inject
@AndroidEntryPoint
class EditProfileActivity :
    BaseActivity<EditProfileViewModel>() {
    companion object {
        const val TAG = "EditProfileActivity"
        const val RESULT_GALLERY_IMG = 1001
    }
//
//    @Inject
//    lateinit var camera: Camera

    @Inject
    lateinit var editProfileBroadcast: EditProfileBroadcast

    @Inject
    @SavingProfileDialog
    lateinit var savingProfileDialog: ProgressDialogFragment

    @Inject
    @UploadImageDialog
    lateinit var uploadImageDialog: ProgressDialogFragment

    @Inject
    lateinit var discardChangeDialogFragment: DiscardChangeDialogFragment

    @Inject
    lateinit var discardChangeViewModel: DiscardChangeViewModel

    @Inject
    lateinit var imageSelectionDialogFragment: ImageSelectionDialogFragment

    @Inject
    lateinit var imageSelectionViewModel: ImageSelectionViewModel

    override fun provideLayoutId() = R.layout.activity_edit_profile

    override fun setupView(savedInstanceState: Bundle?) {
        //set photo, name and tag line and email of user and set click on done (right) icon
        showToolBar(View.VISIBLE)
        setToolbarTitle(R.string.edit_profile)
        setToolbarStart(R.drawable.ic_cancel)
        setToolbarEnd(R.drawable.ic_tick)

        etName.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onNameChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })

        etBio.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onBioChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        })


        tb_end_image.setOnClickListener { viewModel.onSave() }
        tb_start_image.setOnClickListener { viewModel.onCancel() }
        parentImage.setOnClickListener { viewModel.onChangeImage() }

    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.nameFieldValidation.observe(this, {
            when (it.status) {
                Status.ERROR -> etName.error = it.data?.run { getString(this) }
                else -> etName.error = null
            }
        })

        viewModel.nameField.observe(this, {
            if (etName.text.toString() != it) etName.setText(it)
        })

        viewModel.bioField.observe(this, {
            if (etBio.text.toString() != it) etBio.setText(it)
        })

        viewModel.email.observe(this, {
            if (tvEmail.text.toString() != it) tvEmail.text = it
        })

        viewModel.profileUrl.observe(this, {
            it?.run {

                val glideRequest = Glide
                    .with(ivEditProfile.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val params = parentImage.ivProfile.layoutParams as ViewGroup.LayoutParams
                    params.width = placeholderWidth
                    params.height = placeholderHeight
                    ivEditProfile.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_unselected))
                }
                glideRequest.into(ivEditProfile)
            }
        })

        viewModel.imageUpdateLoading.observe(this, {
            if (it) showImageUpdateDialog() else hideImageUpdateDialog()
        })

        viewModel.profileUpdateLoading.observe(this, {
            if (it) showProfileUpdateDialog() else hideProfileUpdateDialog()
        })

        viewModel.loading.observe(this, {
            pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.openImageSelectionDialog.observe(this, {
            it.getIfNotHandled()?.run {
                showImageSelectionDialog()
            }
        })

        viewModel.showDiscardDialog.observe(this, {
            it.getIfNotHandled()?.run {
                if (this) {
                    showDiscardChangesDialog()
                } else {
                    finish()
                }
            }
        })

        viewModel.updatedUser.observe(this, {
            it.getIfNotHandled()?.run {
                editProfileBroadcast.userInfo.postValue(this)
            }
        })

        imageSelectionViewModel.onGallerySelected.observe(this, {
            it.getIfNotHandled()?.run {
                Intent(Intent.ACTION_PICK)
                    .apply {
                        type = "image/*"
                    }.run {
                        startActivityForResult(this, RESULT_GALLERY_IMG)
                    }
            }
        })

        imageSelectionViewModel.onCameraSelected.observe(this, {
            it.getIfNotHandled()?.run {
                try {
//                    camera.takePicture()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        discardChangeViewModel.yesSelected.observe(this, {
            finish()
        })
    }

    private fun hideImageUpdateDialog() {
        Logger.d(TAG, "hideImageUpdateDialog")
        Handler().postDelayed({
            savingProfileDialog.dismiss()
        }, 2000)

    }

    private fun hideProfileUpdateDialog() {
        Logger.d(TAG, "hideProfileUpdateDialog")
        Handler().postDelayed({
            uploadImageDialog.dismiss()
            finish()
        }, 2000)
    }

    private fun showImageSelectionDialog() {
        Logger.d(TAG, "showImageSelectionDialog")
        imageSelectionDialogFragment.show(supportFragmentManager, ImageSelectionDialogFragment.TAG)
    }

    private fun showImageUpdateDialog() {
        Logger.d(TAG, "showImageUpdateDialog")
        savingProfileDialog.show(supportFragmentManager, ProgressDialogFragment.TAG)
    }

    private fun showProfileUpdateDialog() {
        Logger.d(TAG, "showProfileUpdateDialog")
        uploadImageDialog.show(supportFragmentManager, ProgressDialogFragment.TAG)
    }

    private fun showDiscardChangesDialog() {
        Logger.d(TAG, "showDiscardChangesDialog")
        discardChangeDialogFragment.show(supportFragmentManager, DiscardChangeDialogFragment.TAG)
    }

    override fun onActivityResult(reqCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(reqCode, resultCode, intent)
        if (resultCode == Activity.RESULT_OK) {
            when (reqCode) {
                RESULT_GALLERY_IMG -> {
                    try {
                        intent?.data?.let {
                            contentResolver?.openInputStream(it)?.run {
                                viewModel.onGalleryImageSelected(this)
                            }
                        } ?: showMessage(R.string.try_again)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                        showMessage(R.string.try_again)
                    }
                }
//                Camera.REQUEST_TAKE_PHOTO -> {
//                    viewModel.onCameraImageTaken { camera.cameraBitmapPath }
//                }
            }
        }
    }
}