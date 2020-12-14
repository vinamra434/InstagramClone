package com.mindorks.bootcamp.instagram.di.module

import androidx.lifecycle.ViewModelProvider
import com.mindorks.bootcamp.instagram.ui.base.BaseDialogFragment
import com.mindorks.bootcamp.instagram.ui.discardchanges.DiscardChangeViewModel
import com.mindorks.bootcamp.instagram.ui.imageselection.ImageSelectionViewModel
import com.mindorks.bootcamp.instagram.ui.progressdialog.ProgressViewModel
import com.mindorks.bootcamp.instagram.utils.ViewModelProviderFactory
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable

@Module
class DialogFragmentModule(private val baseDialogFragment: BaseDialogFragment<*>) {

    @Provides
    fun provideProgressViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
    ): ProgressViewModel = ViewModelProvider(baseDialogFragment, ViewModelProviderFactory(
        ProgressViewModel::class
    ) {
        ProgressViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(ProgressViewModel::class.java)

    @Provides
    fun provideDiscardChangeViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
    ): DiscardChangeViewModel = ViewModelProvider(baseDialogFragment.requireActivity(), ViewModelProviderFactory(
        DiscardChangeViewModel::class
    ) {
        DiscardChangeViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(DiscardChangeViewModel::class.java)

    @Provides
    fun provideImageSelectionViewModel(
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable,
        networkHelper: NetworkHelper,
    ): ImageSelectionViewModel = ViewModelProvider(baseDialogFragment.requireActivity(), ViewModelProviderFactory(
        ImageSelectionViewModel::class
    ) {
        ImageSelectionViewModel(schedulerProvider, compositeDisposable, networkHelper)
    }).get(ImageSelectionViewModel::class.java)

}