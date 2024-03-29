package com.mindorks.bootcamp.instagram.ui.discardchanges

import android.os.Bundle
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.DialogFragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_dialog_discard_change.*

class DiscardChangeDialogFragment : BaseDialogFragment<DiscardChangeViewModel>() {

    companion object {
        const val TAG = "DiscardChangeDialogFragment"

        fun newInstance(): DiscardChangeDialogFragment {
            val args = Bundle()
            val fragment = DiscardChangeDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent) {
        dialogFragmentComponent.inject(this)
    }

    override fun getCancelable() = true

    override fun provideLayout() = R.layout.fragment_dialog_discard_change

    override fun setupView() {
        tv_yes.setOnClickListener { viewModel.onYesClicked() }
        tv_no.setOnClickListener {
            dismiss()
        }
    }

    override fun setupObservers() {}
}