package com.mindorks.bootcamp.instagram.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.data.model.MyPost
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.editprofile.EditProfileActivity
import com.mindorks.bootcamp.instagram.ui.editprofile.EditProfileBroadcast
import com.mindorks.bootcamp.instagram.ui.login.LoginActivity
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.ui.profile.myposts.MyPostsAdapter
import com.mindorks.bootcamp.instagram.ui.profile.myposts.MyPostDetailSharedViewModel
import com.mindorks.bootcamp.instagram.ui.postdetail.PostDetailBroadcast
import com.mindorks.bootcamp.instagram.ui.postdetail.PostDetailFragment
import com.mindorks.bootcamp.instagram.utils.common.ClickListener
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.common.GlideHelper
import com.mindorks.bootcamp.instagram.utils.log.Logger
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.progressBar
import javax.inject.Inject


class ProfileFragment : BaseFragment<ProfileViewModel>(), ClickListener {

    companion object {

        const val TAG = "ProfileFragment"

        fun newInstance(): ProfileFragment {
            val args = Bundle()
            val fragment = ProfileFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var editProfileBroadcast: EditProfileBroadcast

    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    @Inject
    lateinit var myPostsAdapter: MyPostsAdapter

    @Inject
    lateinit var myPostDetailSharedViewModel: MyPostDetailSharedViewModel

    @Inject
    lateinit var postDetailBroadcast: PostDetailBroadcast

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    override fun provideLayoutId(): Int = R.layout.fragment_profile

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.myPosts.observe(this, {
            it.data?.run {
                tvPostCount.text = size.toString()
                myPostsAdapter.appendData(this)
            }
        })

        viewModel.loading.observe(this, {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.userName.observe(this, Observer {
            tvProfileName.text = it
        })

        viewModel.tagLine.observe(this, Observer {
            tvProfileDescription.text = it
        })

        viewModel.profileImage.observe(this, Observer {
            it?.run {
                val glideRequest = Glide
                    .with(ivProfile.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val params = ivProfile.layoutParams as ViewGroup.LayoutParams
                    params.width = placeholderWidth
                    params.height = placeholderHeight
                    ivProfile.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_unselected))
                }
                glideRequest.into(ivProfile)
            }
        })

        viewModel.launchEditProfile.observe(this, Observer {
            it.getIfNotHandled()?.run {
                startActivity(Intent(activity, EditProfileActivity::class.java))
            }
        })

        editProfileBroadcast.userName.observe(this, Observer {
            tvProfileName.text = it
        })

        editProfileBroadcast.tagLine.observe(this, Observer {
            tvProfileDescription.text = it
        })

        editProfileBroadcast.profileImage.observe(this, Observer {
            it?.run {
                val glideRequest = Glide
                    .with(ivProfile.context)
                    .load(GlideHelper.getProtectedUrl(url, headers))
                    .apply(RequestOptions.circleCropTransform())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_selected))

                if (placeholderWidth > 0 && placeholderHeight > 0) {
                    val params = ivProfile.layoutParams as ViewGroup.LayoutParams
                    params.width = placeholderWidth
                    params.height = placeholderHeight
                    ivProfile.layoutParams = params
                    glideRequest
                        .apply(RequestOptions.overrideOf(placeholderWidth, placeholderHeight))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_profile_unselected))
                }
                glideRequest.into(ivProfile)
            }
        })

        viewModel.logout.observe(this, Observer {
            it.getIfNotHandled()?.run {
                goToLoginActivity()
            }
        })

        viewModel.launchPostDetail.observe(this, Observer {
            it.getIfNotHandled()?.run {
                showPostDetail(this.id)
                myPostDetailSharedViewModel.data.postValue(Event(this.id))
            }
        })

        postDetailBroadcast.postDeleted.observe(this, Observer {
            viewModel.onDeletePost(it)
        })

        viewModel.refreshDeletePost.observe(this, Observer {
            it.data?.run {
                myPostsAdapter.updateList(this)
                tvPostCount.text = size.toString()
            }
        })

        mainSharedViewModel.newPost.observe(this, Observer {
            viewModel.onNewPost(
                MyPost(
                    it.createdAt,
                    it.id,
                    it.imageHeight,
                    it.imageUrl,
                    it.imageWidth
                )
            )
        })


        viewModel.refreshPosts.observe(this, Observer {
            it.data?.run {
                myPostsAdapter.updateList(this)
            }
        })

    }

    private fun showPostDetail(postId: String) {

        val fragmentTransaction =
            activity?.supportFragmentManager?.beginTransaction()

        var fragment =
            activity?.supportFragmentManager?.findFragmentByTag(PostDetailFragment.TAG) as PostDetailFragment?

        if (fragment == null) {
            Logger.d(TAG, "new instance of profile detail")
            fragment = PostDetailFragment.newInstance(postId)
            fragmentTransaction?.add(R.id.containerFragment, fragment, PostDetailFragment.TAG)
        }

        fragmentTransaction?.hide(this)

        fragmentTransaction?.addToBackStack(PostDetailFragment.TAG)
        fragmentTransaction?.commit()
    }

    private fun goToLoginActivity() {

        Intent(
            activity,
            LoginActivity::class.java
        ).run { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) }
            .also { startActivity(it) }

        activity?.finish()
    }

    override fun setupView(view: View) {
        btnEditProfile.setOnClickListener { viewModel.onEditClicked() }
        tvLogout.setOnClickListener { viewModel.onLogout() }

        rvMyPosts.apply {
            layoutManager = linearLayoutManager
            adapter = myPostsAdapter

        }
    }

    override fun onClick(adapterPosition: Int) {
        viewModel.onPostImageClicked(adapterPosition)
    }
}