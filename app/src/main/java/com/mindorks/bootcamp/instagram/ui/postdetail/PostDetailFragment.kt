package com.mindorks.bootcamp.instagram.ui.postdetail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.profile.myposts.MyPostDetailSharedViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.bootcamp.instagram.utils.log.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.custom_toolbar.*
import kotlinx.android.synthetic.main.fragment_post_detail.*
import javax.inject.Inject

@AndroidEntryPoint

class PostDetailFragment : BaseFragment<PostDetailViewModel>() {

    companion object {

        const val TAG = "PostDetailFragment"

        private const val POST_ID = "param1"

        fun newInstance(postId: String): PostDetailFragment =
            PostDetailFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(POST_ID, postId)
                    }
                }
    }

    @Inject
    lateinit var myPostDetailSharedViewModel: MyPostDetailSharedViewModel

    @Inject
    lateinit var postDetailBroadcast: PostDetailBroadcast

    override fun provideLayoutId() = R.layout.fragment_post_detail

    override fun setupView(view: View) {
        tb_start_image.setBackgroundResource(R.drawable.ic_back)
        tb_text.text = getString(R.string.post)

        tb_start_image.setOnClickListener { super.goBack() }

        ivLike.setOnClickListener { viewModel.onLikeClick() }

        ivDelete.setOnClickListener { viewModel.onDeletePost(arguments?.getString(POST_ID, null)) }
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.imageDeleted.observe(this, Observer {
            it.getIfNotHandled()?.run {
                postDetailBroadcast.postDeleted.postValue(this)
                super.goBack()
            }
        })

        myPostDetailSharedViewModel.data.observe(this, Observer {
            it.getIfNotHandled()?.run { viewModel.getPostDetail(this) }
        })

        viewModel.loading.observe(this, Observer {
            pb_loading.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.name.observe(this, Observer {
            Logger.d(TAG, "name")
            tvName.text = it
        })

        viewModel.postTime.observe(this, Observer {
            Logger.d(TAG, "postTime")
            tvTime.text = it
        })

        viewModel.likesCount.observe(this, Observer {
            Logger.d(TAG, "likesCount")
            tvLikesCount.text = getString(R.string.post_like_label, it)
        })

        viewModel.isLiked.observe(this, Observer {
            Logger.d(TAG, "isLiked")
            if (it) ivLike.setImageResource(R.drawable.ic_heart_selected)
            else ivLike.setImageResource(R.drawable.ic_heart_unselected)
        })

        viewModel.profileImage.observe(this, Observer {

            lateinit var glideRequest: RequestBuilder<Drawable>
            if (it != null) {
                glideRequest = Glide
                    .with(ivProfile.context)
                    .load(GlideHelper.getProtectedUrl(it.url, it.headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))

                if (it.placeholderWidth > 0 && it.placeholderHeight > 0) {
                    val params = ivProfile.layoutParams as ViewGroup.LayoutParams
                    params.width = it.placeholderWidth
                    params.height = it.placeholderHeight
                    ivProfile.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(it.placeholderWidth, it.placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_unselected))
                }
            } else {
                glideRequest = Glide
                    .with(ivProfile.context)
                    .load(R.drawable.ic_signup)
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))
            }
            glideRequest.into(ivProfile)

        })

        viewModel.imageDetail.observe(this, Observer {
            it?.run {
                val glideRequest = Glide
                    .with(ivPost.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val params = ivPost.layoutParams as ViewGroup.LayoutParams
                    params.width = placeholderWidth
                    params.height = placeholderHeight
                    ivPost.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_photo))
                }
                glideRequest.into(ivPost)
            }
        })


        viewModel.sendPostLike.observe(this, Observer {
            postDetailBroadcast.postLiked.postValue(Event(it))
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy")
    }
}