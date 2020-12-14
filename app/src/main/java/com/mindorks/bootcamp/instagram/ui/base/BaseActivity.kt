package com.mindorks.bootcamp.instagram.ui.base

import android.os.Bundle
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import com.mindorks.bootcamp.instagram.InstagramApplication
import com.mindorks.bootcamp.instagram.R
import com.mindorks.bootcamp.instagram.di.component.ActivityComponent
import com.mindorks.bootcamp.instagram.di.component.DaggerActivityComponent
import com.mindorks.bootcamp.instagram.di.module.ActivityModule
import com.mindorks.bootcamp.instagram.utils.display.Toaster
import com.mindorks.bootcamp.instagram.utils.log.Logger
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import javax.inject.Inject

abstract class BaseActivity<VM : BaseViewModel> : AppCompatActivity() {

    @Inject
    lateinit var viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies(buildActivityComponent())
        super.onCreate(savedInstanceState)
        setContentView(provideLayoutId())
        setupObservers()
        setupView(savedInstanceState)
        if (savedInstanceState == null) viewModel.onCreate() else Logger.d("activity", "device rotated")
    }

    private fun buildActivityComponent() =
        DaggerActivityComponent
            .builder()
            .applicationComponent((application as InstagramApplication).applicationComponent)
            .activityModule(ActivityModule(this))
            .build()

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })

        viewModel.messageStringId.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })
    }

    fun showMessage(message: String) = Toaster.show(applicationContext, message)

    fun showMessage(@StringRes resId: Int) = showMessage(getString(resId))

    open fun goBack() = onBackPressed()

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStackImmediate()
        else super.onBackPressed()
    }

    @LayoutRes
    protected abstract fun provideLayoutId(): Int

    protected abstract fun injectDependencies(activityComponent: ActivityComponent)

    protected abstract fun setupView(savedInstanceState: Bundle?)

    override fun setContentView(layoutResID: Int) {
        val coordinatorLayout =
            layoutInflater.inflate(R.layout.activity_base, null) as CoordinatorLayout
        var activityContainer: FrameLayout = coordinatorLayout.findViewById(R.id.layout_container)

        layoutInflater.inflate(layoutResID, activityContainer, true)

        super.setContentView(coordinatorLayout)
    }

    fun showToolBar(visibility: Int) {
        toolbar.visibility = visibility
    }

    fun setToolbarTitle(resId: Int) {
        tb_text.text = getString(resId)
    }

    fun setToolbarEnd(resId: Int) {
        tb_end_image.setBackgroundResource(resId)
    }

    fun setToolbarStart(resId: Int) {
        tb_start_image.setBackgroundResource(resId)
    }
}