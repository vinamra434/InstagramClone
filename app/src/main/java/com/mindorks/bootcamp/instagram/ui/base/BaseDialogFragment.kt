package com.mindorks.bootcamp.instagram.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import javax.inject.Inject

abstract class BaseDialogFragment<VM : BaseViewModel> : DialogFragment() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
