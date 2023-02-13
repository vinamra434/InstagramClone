package com.mindorks.bootcamp.instagram.di_hilt.di.module

import androidx.lifecycle.LifecycleRegistry
import com.mindorks.bootcamp.instagram.ui.base.BaseItemViewHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewComponent

@InstallIn(ViewComponent::class)
@Module
object ViewHolderModule {

    @Provides
    fun provideLifecycleRegistry(viewHolder: BaseItemViewHolder<*, *>): LifecycleRegistry =
        LifecycleRegistry(viewHolder)
}