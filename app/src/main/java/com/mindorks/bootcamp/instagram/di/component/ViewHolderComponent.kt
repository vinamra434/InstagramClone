package com.mindorks.bootcamp.instagram.di.component

import com.mindorks.bootcamp.instagram.di.ViewModelScope
import com.mindorks.bootcamp.instagram.di.module.ViewHolderModule
import com.mindorks.bootcamp.instagram.ui.home.posts.PostItemViewHolder
import com.mindorks.bootcamp.instagram.ui.profile.myposts.MyPostItemViewHolder
import dagger.Component

/*every component needs a scope*/

@ViewModelScope
@Component(
    dependencies = [ApplicationComponent::class],
    modules = [ViewHolderModule::class]
)
interface ViewHolderComponent {

    fun inject(viewHolder: PostItemViewHolder)

    fun inject(viewHolder: MyPostItemViewHolder)
}