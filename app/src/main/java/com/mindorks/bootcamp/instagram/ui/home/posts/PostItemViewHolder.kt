package com.mindorks.bootcamp.instagram.ui.home.posts

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.Post
import com.mindorks.bootcamp.instagram.di.component.ViewHolderComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseItemViewHolder
import com.mindorks.bootcamp.instagram.ui.postdetail.PostDetailBroadcast
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.bootcamp.instagram.utils.log.Logger
import kotlinx.android.synthetic.main.item_view_post.view.*
import javax.inject.Inject

class PostItemViewHolder(
    parent: ViewGroup
) : BaseItemViewHolder<Post, PostItemViewModel>(
    R.layout.item_view_post,
    parent
) {
    @Inject
    lateinit var postDetailBroadcast: PostDetailBroadcast


    override fun injectDependencies(viewHolderComponent: ViewHolderComponent) {
        viewHolderComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.name.observe(this, Observer {
            Logger.d("PostItemViewHolder", "name = $it")
            itemView.tvName.text = it
        })

        viewModel.postTime.observe(this, Observer {
            Logger.d("PostItemViewHolder", "postTime = $it")
            itemView.tvTime.text = it
        })

        viewModel.likesCount.observe(this, Observer {
            Logger.d("PostItemViewHolder", "likesCount = $it")
            itemView.tvLikesCount.text = itemView.context.getString(R.string.post_like_label, it)
        })

        viewModel.isLiked.observe(this, Observer {
            Logger.d("PostItemViewHolder", "isLiked = $it")
            if (it) itemView.ivLike.setImageResource(R.drawable.ic_heart_selected)
            else itemView.ivLike.setImageResource(R.drawable.ic_heart_unselected)
        })

        viewModel.profileImage.observe(this, Observer {
            lateinit var glideRequest: RequestBuilder<Drawable>
            if (it != null) {
                Logger.d("PostItemViewHolder", "profileImage = ${it.url}")
                glideRequest = Glide
                    .with(itemView.ivProfile.context)
                    .load(GlideHelper.getProtectedUrl(it.url, it.headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))

                if (it.placeholderWidth > 0 && it.placeholderHeight > 0) {
                    val params = itemView.ivProfile.layoutParams as ViewGroup.LayoutParams
                    params.width = it.placeholderWidth
                    params.height = it.placeholderHeight
                    itemView.ivProfile.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(it.placeholderWidth, it.placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_unselected))
                }
            } else {
                glideRequest = Glide
                    .with(itemView.ivProfile.context)
                    .load(R.drawable.ic_signup)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))
            }
            glideRequest.into(itemView.ivProfile)
        })

        viewModel.imageDetail.observe(this, Observer {
            it?.run {
                Logger.d("PostItemViewHolder", "imageDetail = ${it.url}")
                val glideRequest = Glide
                    .with(itemView.ivPost.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val params = itemView.ivPost.layoutParams as ViewGroup.LayoutParams
                    params.width = placeholderWidth
                    params.height = placeholderHeight
                    itemView.ivPost.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_photo))
                }
                glideRequest.into(itemView.ivPost)
            }
        })

        postDetailBroadcast.postLiked.observe(this, Observer {
            it.getIfNotHandled()?.run { viewModel.postLikeReceived(this) }
        })
    }

    override fun setupView(view: View) {
        itemView.ivLike.setOnClickListener { viewModel.onLikeClick() }
    }
}