package com.mindorks.bootcamp.instagram.ui.imageselection

import android.os.Bundle
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.DialogFragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_image_selection.*

class ImageSelectionDialogFragment : BaseDialogFragment<ImageSelectionViewModel>() {

    companion object {
        const val TAG = "ImageSelectionDialogFragment"

        fun newInstance(): ImageSelectionDialogFragment {
            val args = Bundle()
            val fragment = ImageSelectionDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent) {
        dialogFragmentComponent.inject(this)
    }

    override fun getCancelable() = true

    override fun provideLayout() = R.layout.fragment_dialog_image_selection

    override fun setupView() {
        tv_from_camera.setOnClickListener { viewModel.onCameraSelected()}
        tv_from_gallery.setOnClickListener { viewModel.onGallerySelected()}
    }

    override fun setupObservers() {}
}