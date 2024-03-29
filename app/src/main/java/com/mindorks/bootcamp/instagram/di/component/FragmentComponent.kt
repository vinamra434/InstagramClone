package com.mindorks.bootcamp.instagram.di.component

import com.mindorks.bootcamp.instagram.di.FragmentScope
import com.mindorks.bootcamp.instagram.di.module.FragmentModule
import com.mindorks.bootcamp.instagram.ui.home.HomeFragment
import com.mindorks.bootcamp.instagram.ui.photo.PhotoFragment
import com.mindorks.bootcamp.instagram.ui.profile.ProfileFragment
import com.mindorks.bootcamp.instagram.ui.postdetail.PostDetailFragment
import dagger.Component

/*every component needs a scope*/

@FragmentScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [FragmentModule::class]
)
interface FragmentComponent {


    fun inject(fragment: ProfileFragment)

    fun inject(fragment: PhotoFragment)

    fun inject(fragment: HomeFragment)

    fun inject(fragment: PostDetailFragment)
}