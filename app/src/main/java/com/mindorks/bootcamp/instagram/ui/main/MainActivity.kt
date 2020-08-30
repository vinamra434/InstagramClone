package com.mindorks.bootcamp.instagram.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.ui.base.BaseActivity
import com.mindorks.bootcamp.instagram.ui.home.HomeFragment
import com.mindorks.bootcamp.instagram.ui.photo.PhotoFragment
import com.mindorks.bootcamp.instagram.ui.profile.ProfileFragment
import com.mindorks.bootcamp.instagram.ui.postdetail.PostDetailFragment
import com.mindorks.bootcamp.instagram.utils.log.Logger
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity<MainViewModel>() {

    companion object {
        const val TAG = "MainActivity"
        private const val ACTIVE_FRAGMENT = "active_fragment"
    }

    @Inject
    lateinit var mainSharedViewModel: MainSharedViewModel

    private var activeFragment: Fragment? = null

    override fun provideLayoutId(): Int = R.layout.activity_main

    override fun injectDependencies(activityComponent: ActivityComponent) {
        Logger.d(TAG, "injectDependencies")
        activityComponent.inject(this)
    }

    override fun setupView(savedInstanceState: Bundle?) {
        Logger.d(TAG, "setupView")
        savedInstanceState?.run {

            when (getString(ACTIVE_FRAGMENT)) {
                HomeFragment.TAG -> {
                    activeFragment = supportFragmentManager.findFragmentByTag(HomeFragment.TAG) as HomeFragment?
                    viewModel.onHomeSelected()
                }
                ProfileFragment.TAG -> {
                    activeFragment = supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) as ProfileFragment?
                    viewModel.onProfileSelected()
                }
                PhotoFragment.TAG -> {
                    activeFragment = supportFragmentManager.findFragmentByTag(PhotoFragment.TAG) as PhotoFragment?
                    viewModel.onPhotoSelected()
                }
            }

        }

        bottomNavigation?.run {
            itemIconTintList = null
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.itemHome -> {
                        viewModel.onHomeSelected()
                        true
                    }
                    R.id.itemAddPhotos -> {
                        viewModel.onPhotoSelected()
                        true
                    }
                    R.id.itemProfile -> {
                        viewModel.onProfileSelected()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun setupObservers() {
        super.setupObservers()
        Logger.d(TAG, "setupObservers")

        viewModel.homeNavigation.observe(this, Observer {
            it.getIfNotHandled()?.run { showHome() }
        })

        viewModel.profileNavigation.observe(this, Observer {
            it.getIfNotHandled()?.run { showProfile() }
        })

        viewModel.photoNavigation.observe(this, Observer {
            it.getIfNotHandled()?.run { showAddPhoto() }
        })

        mainSharedViewModel.homeRedirection.observe(this, Observer {
            it.getIfNotHandled()?.run { bottomNavigation.selectedItemId = R.id.itemHome }
        })

    }

    private fun showHome() {
        Logger.d(TAG, "showHome")

        if (activeFragment is HomeFragment) return

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var fragment = supportFragmentManager.findFragmentByTag(HomeFragment.TAG) as HomeFragment?
        val postDetailFragment =
            supportFragmentManager.findFragmentByTag(PostDetailFragment.TAG) as PostDetailFragment?

        if (fragment == null) {
            fragment = HomeFragment.newInstance()
            fragmentTransaction.add(R.id.containerFragment, fragment, HomeFragment.TAG)
        } else {
            fragmentTransaction.show(fragment)
        }

        if (activeFragment != null) {
            fragmentTransaction.hide(activeFragment as Fragment)
        }

        if (postDetailFragment != null) fragmentTransaction.hide(postDetailFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment

    }


    private fun showProfile() {
        Logger.d(TAG, "showProfile")

        if (activeFragment is ProfileFragment) return

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var fragment =
            supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) as ProfileFragment?
        val postDetailFragment =
            supportFragmentManager.findFragmentByTag(PostDetailFragment.TAG) as PostDetailFragment?

        if (fragment == null) {
            fragment = ProfileFragment.newInstance()
            fragmentTransaction.add(R.id.containerFragment, fragment, ProfileFragment.TAG)
        } else {
            if (postDetailFragment != null) {
                fragmentTransaction.show(postDetailFragment)
            } else {
                fragmentTransaction.show(fragment)
            }
        }

        if (activeFragment != null) fragmentTransaction.hide(activeFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment


    }

    private fun showAddPhoto() {
        Logger.d(TAG, "showAddPhoto")

        if (activeFragment is PhotoFragment) return

        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var fragment = supportFragmentManager.findFragmentByTag(PhotoFragment.TAG) as PhotoFragment?
        val postDetailFragment =
            supportFragmentManager.findFragmentByTag(PostDetailFragment.TAG) as PostDetailFragment?

        if (fragment == null) {
            fragment = PhotoFragment.newInstance()
            fragmentTransaction.add(R.id.containerFragment, fragment, PhotoFragment.TAG)
        } else {
            fragmentTransaction.show(fragment)
        }

        if (activeFragment != null) fragmentTransaction.hide(activeFragment as Fragment)
        if (postDetailFragment != null) fragmentTransaction.hide(postDetailFragment as Fragment)

        fragmentTransaction.commit()

        activeFragment = fragment

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

    override fun onResume() {
        super.onResume()
        Logger.d(TAG, "onResume")
    }

    override fun onPause() {
        super.onPause()
        Logger.d(TAG, "onPause")
    }

    override fun onStop() {
        super.onStop()
        Logger.d(TAG, "onStop")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Logger.d(TAG, "onSaveInstanceState")
        outState.putString(ACTIVE_FRAGMENT, activeFragment?.tag)
    }


}