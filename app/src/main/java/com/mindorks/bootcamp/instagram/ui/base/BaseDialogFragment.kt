package com.mindorks.bootcamp.instagram.ui.base

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.mindorks.bootcamp.instagram.InstagramApplication
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.DaggerDialogFragmentComponent
import com.mindorks.bootcamp.instagram.di.component.DialogFragmentComponent
import com.mindorks.bootcamp.instagram.di.module.DialogFragmentModule
import javax.inject.Inject

abstract class BaseDialogFragment<VM : BaseViewModel> : DialogFragment() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildDialogFragmentComponent());
        super.onCreate(savedInstanceState)
    }

    abstract fun injectDependencies(dialogFragmentComponent: DialogFragmentComponent)

    private fun buildDialogFragmentComponent(): DialogFragmentComponent {
        return DaggerDialogFragmentComponent
            .builder()
            .applicationComponent((activity?.application as InstagramApplication).applicationComponent)
            .dialogFragmentModule(DialogFragmentModule(this))
            .build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCancelable = getCancelable()
        return inflater.inflate(provideLayout(), container, false)
    }

    abstract fun getCancelable(): Boolean

    @LayoutRes
    protected abstract fun provideLayout(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupObservers()
    }

    protected abstract fun setupView()

    protected abstract fun setupObservers()
}
