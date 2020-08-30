package com.mindorks.bootcamp.instagram.ui.editprofile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.ImageDialog
import com.mindorks.bootcamp.instagram.di.ProfileDialog
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.utils.common.*
import com.mindorks.bootcamp.instagram.utils.log.Logger
import com.mindorks.paracamera.Camera
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.item_view_post.view.*
import java.io.FileNotFoundException
import javax.inject.Inject

class EditProfileActivity : BaseActivity<EditProfileViewModel>(),
    DialogListener, DialogDiscardChanges.DialogListener {

    companion object {
        const val TAG = "EditProfileActivity"
        const val RESULT_GALLERY_IMG = 1001
    }

    @Inject
    lateinit var camera: Camera

    @Inject
    lateinit var editProfileBroadcast: EditProfileBroadcast

    @Inject
    @ImageDialog
    lateinit var imageUpdateDialog: DialogSavingDetail

    @Inject
    @ProfileDialog
    lateinit var profileUpdateDialog: DialogSavingDetail

    override fun provideLayoutId() = R.layout.activity_edit_profile

    override fun injectDependencies(activityComponent: ActivityComponent) =
        activityComponent.inject(this)

    override fun setupView(savedInstanceState: Bundle?) {
        //set photo, name and tagline and email of user and set click on done (right) icon

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

        viewModel.nameFieldValidation.observe(this, Observer {
            when (it.status) {
                Status.ERROR -> etName.error = it.data?.run { getString(this) }
                else -> etName.error = null
            }
        })

        viewModel.nameField.observe(this, Observer {
            if (etName.text.toString() != it) etName.setText(it)
        })

        viewModel.bioField.observe(this, Observer {
            if (etBio.text.toString() != it) etBio.setText(it)
        })

        viewModel.email.observe(this, Observer {
            if (tvEmail.text.toString() != it) tvEmail.text = it
        })

        viewModel.profileUrl.observe(this, Observer {
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

        viewModel.imageUpdateLoading.observe(this, Observer {
            if (it) showImageUpdateDialog() else hideImageUpdateDialog()
        })

        viewModel.profileUpdateLoading.observe(this, Observer {
            if (it) showProfileUpdateDialog() else hideProfileUpdateDialog()
        })

        viewModel.loading.observe(this, Observer {
            pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.openImageSelectionDialog.observe(this, Observer {
            it.getIfNotHandled()?.run {
                showImageSelectionDialog()
            }
        })

        viewModel.showDiscardDialog.observe(this, Observer {
            it.getIfNotHandled()?.run {
                if (this) {
                    showDiscardChangesDialog()
                } else {
                    finish()
                }
            }
        })

        viewModel.updatedUser.observe(this, Observer {
            it.getIfNotHandled()?.run {
                    editProfileBroadcast.userInfo.postValue(this)
            }
        })

    }

    private fun hideImageUpdateDialog() {
        Handler().postDelayed({
            imageUpdateDialog.dismiss()
        },2000)
    }

    private fun hideProfileUpdateDialog() {
        Handler().postDelayed({
            profileUpdateDialog.dismiss()
            finish()
        },2000)
    }

    private fun showImageSelectionDialog() {

        val ft = supportFragmentManager.beginTransaction()
        val imageSelectionFragment = DialogImageSelection.newInstance()
        imageSelectionFragment.show(ft, DialogImageSelection.TAG)
    }

    private fun showImageUpdateDialog() {

        val ft = supportFragmentManager.beginTransaction()
        imageUpdateDialog.show(ft, DialogSavingDetail.TAG)
    }

    private fun showProfileUpdateDialog() {

        val ft = supportFragmentManager.beginTransaction()
        profileUpdateDialog.show(ft, DialogSavingDetail.TAG)
    }

    private fun showDiscardChangesDialog() {

        val ft = supportFragmentManager.beginTransaction()
        val fragment: Fragment? =
            supportFragmentManager.findFragmentByTag(DialogDiscardChanges.TAG) as DialogDiscardChanges?

        if (fragment != null) {
            ft.remove(fragment)
        }
        ft.addToBackStack(null)

        val discardImageDialog = DialogDiscardChanges.newInstance()
        discardImageDialog.show(ft, DialogDiscardChanges.TAG)

    }

    override fun onOptionSelected(option: DialogListener.Selection) {
        when (option) {
            DialogListener.Selection.CAMERA -> {
                try {
                    camera.takePicture()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            DialogListener.Selection.GALLERY -> {
                Intent(Intent.ACTION_PICK)
                    .apply {
                        type = "image/*"
                    }.run {
                        startActivityForResult(this, RESULT_GALLERY_IMG)
                    }
            }
        }
    }

    override fun onOptionSelected(option: DialogDiscardChanges.Choice) {
        if (option == DialogDiscardChanges.Choice.YES) {
            finish()
        }
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
                Camera.REQUEST_TAKE_PHOTO -> {
                    viewModel.onCameraImageTaken { camera.cameraBitmapPath }
                }
            }
        }
    }

    override fun onBackPressed() {
        viewModel.onCancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d("EditProfileActivity", "onDestroy")
    }
}