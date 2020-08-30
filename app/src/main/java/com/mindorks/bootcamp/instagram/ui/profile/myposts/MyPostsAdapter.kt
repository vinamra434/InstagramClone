package com.mindorks.bootcamp.instagram.ui.profile.myposts

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.mindorks.bootcamp.instagram.data.model.MyPost
import com.mindorks.bootcamp.instagram.ui.base.BaseAdapter
import com.mindorks.bootcamp.instagram.utils.common.ClickListener

class MyPostsAdapter(
    parentLifecycle: Lifecycle,
    myPosts: ArrayList<MyPost>,
    private val clickListener: ClickListener
) : BaseAdapter<MyPost, MyPostItemViewHolder>(parentLifecycle, myPosts) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MyPostItemViewHolder(parent, clickListener)
}