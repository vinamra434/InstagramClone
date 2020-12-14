package com.mindorks.bootcamp.instagram.di.component

import com.mindorks.bootcamp.instagram.di.DialogFragmentScope
import com.mindorks.bootcamp.instagram.di.module.DialogFragmentModule
import com.mindorks.bootcamp.instagram.ui.discardchanges.DiscardChangeDialogFragment
import com.mindorks.bootcamp.instagram.ui.imageselection.ImageSelectionDialogFragment
import com.mindorks.bootcamp.instagram.ui.progressdialog.ProgressDialogFragment
import dagger.Component

@DialogFragmentScope
@Component(

    dependencies = [ApplicationComponent::class],
    modules = [DialogFragmentModule::class])
interface DialogFragmentComponent {

    fun inject(progressDialogFragment: ProgressDialogFragment)
    fun inject(progressDialogFragment: ImageSelectionDialogFragment)
    fun inject(discardChangeDialogFragment: DiscardChangeDialogFragment)

}