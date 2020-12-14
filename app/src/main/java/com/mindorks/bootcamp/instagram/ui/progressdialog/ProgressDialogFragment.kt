package com.mindorks.bootcamp.instagram.ui.progressdialog

import android.os.Bundle
import androidx.annotation.StringRes
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.DialogFragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_loading.*

class ProgressDialogFragment : BaseDialogFragment<ProgressViewModel>() {

    companion object {

        const val TAG = "ProgressDialogFragment"
        private const val LOADING_MESSAGE = "LOADING_MESSAGE";

        fun newInstance(@StringRes message: Int): ProgressDialogFragment {
            val args = Bundle()
            args.putInt(LOADING_MESSAGE, message)
            val fragment = ProgressDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent) {
        dialogFragmentComponent.inject(this)
    }

    override fun getCancelable() = false

    override fun provideLayout(): Int = R.layout.fragment_dialog_loading

    override fun setupView() {
        val stringId = arguments?.getInt(LOADING_MESSAGE)
        stringId?.let { loading_text.text = getString(it) }
    }

    override fun setupObservers() {}
}