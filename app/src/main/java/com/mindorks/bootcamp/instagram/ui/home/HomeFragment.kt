package com.mindorks.bootcamp.instagram.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.FragmentComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseFragment
import com.mindorks.bootcamp.instagram.ui.home.posts.PostsAdapter
import com.mindorks.bootcamp.instagram.ui.main.MainSharedViewModel
import com.mindorks.bootcamp.instagram.ui.postdetail.PostDetailBroadcast
import com.mindorks.bootcamp.instagram.utils.log.Logger
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Inject

class HomeFragment : BaseFragment<HomeViewModel>() {

    companion object {

        const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    @Inject
    lateinit var postDetailBroadcast: PostDetailBroadcast

    @Inject
    lateinit var linearLayoutManager: LinearLayoutManager

    @Inject
    lateinit var postsAdapter: PostsAdapter

    override fun provideLayoutId(): Int = R.layout.fragment_home

    override fun injectDependencies(fragmentComponent: FragmentComponent) {
        fragmentComponent.inject(this)
    }

    override fun setupObservers() {
        super.setupObservers()

        viewModel.loading.observe(this, Observer {
            Logger.d(TAG, "loading")

            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.fullPostList.observe(this, Observer {
            it.data?.run {
                Logger.d(TAG, "post list recieved")
                for (i in 0 until size) {
                    Logger.d(TAG, "----------postId = ${this[i].id}")
                }
                Logger.d(TAG, "")

                postsAdapter.updateList(this)
            }
        })

        postDetailBroadcast.postDeleted.observe(this, Observer {

            viewModel.onDeletePost(it)
        })

        
        mainSharedViewModel.newPost.observe(this, Observer {

            viewModel.onNewPost(it)
        })


        viewModel.newPostAdded.observe(this, Observer {
            Logger.d(TAG, "refreshLikePosts")

            it.data?.run {
                postsAdapter.updateList(this)
                rvPosts.scrollToPosition(0)
            }
        })

        viewModel.postDeleted.observe(this, Observer {
            Logger.d(TAG, "refreshDeletePost")

            it.data?.run {
                postsAdapter.updateList(this)
            }
        })
    }

    override fun setupView(view: View) {
        rvPosts.apply {
            layoutManager = linearLayoutManager
            adapter = postsAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    layoutManager?.run {
                        Logger.d(
                            TAG,
                            "findLastVisibleItemPosition = ${(this as LinearLayoutManager).findLastVisibleItemPosition()}  and  itemCount = $itemCount"
                        )
                        if (this is LinearLayoutManager
                            && itemCount > 0
                            && itemCount <= findLastVisibleItemPosition() + 1
                        ) {
                            viewModel.onLoadMore()
                        }
                    }
                }
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Logger.d(TAG, "onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.d(TAG, "onDestroy")
    }

    override fun onStart() {
        super.onStart()
        Logger.d(TAG, "onStart")
    }

    override fun onStop() {
        super.onStop()
        Logger.d(TAG, "onStop")
    }

    override fun onResume() {
        super.onResume()
        Logger.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Logger.d(TAG, "onPause")
    }
}