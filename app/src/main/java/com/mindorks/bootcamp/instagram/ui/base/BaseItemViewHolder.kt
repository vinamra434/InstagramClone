package com.mindorks.bootcamp.instagram.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.mindorks.bootcamp.instagram.utils.display.Toaster
import com.mindorks.bootcamp.instagram.utils.log.Logger
import javax.inject.Inject

/*here we are giving lifecycle using LifeCyclerOwner and connecting it using baseadapter.
* we are manually setting lifecycle of itemviewholder using lifecycle callback of adapter class
* */
abstract class BaseItemViewHolder<T : Any, VM : BaseItemViewModel<T>>(
    @LayoutRes layoutId: Int,
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(layoutId, parent, false)
), LifecycleOwner {

    init {
        onCreate()
    }

    @Inject
    lateinit var viewModel: VM

    /*using this we make itemviewholder lifecycler aware*/
    @Inject
    lateinit var lifecycleRegistry: LifecycleRegistry

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    open fun bind(data: T) {
        viewModel.updateData(data)
    }

    /*https://developer.android.com/topic/libraries/architecture/lifecycle*/
    protected fun onCreate() {
        Logger.d("BaseItemViewHolder", "onCreate")
        lifecycleRegistry.currentState = (Lifecycle.State.INITIALIZED)
        lifecycleRegistry.currentState = (Lifecycle.State.CREATED)
        setupObservers()
        setupView(itemView)
    }

    /*https://developer.android.com/topic/libraries/architecture/lifecycle*/
    fun onStart() {
        Logger.d("BaseItemViewHolder", "onStart")
        lifecycleRegistry.currentState = (Lifecycle.State.STARTED)
        lifecycleRegistry.currentState = (Lifecycle.State.RESUMED)
    }

    /*https://developer.android.com/topic/libraries/architecture/lifecycle*/
    fun onStop() {
        Logger.d("BaseItemViewHolder", "onStop")
        lifecycleRegistry.currentState = (Lifecycle.State.STARTED)
        lifecycleRegistry.currentState = (Lifecycle.State.CREATED)
    }

    /*https://developer.android.com/topic/libraries/architecture/lifecycle*/
    fun onDestroy() {
        Logger.d("BaseItemViewHolder", "onDestroy")
        lifecycleRegistry.currentState = (Lifecycle.State.DESTROYED)
    }


    fun showMessage(message: String) = Toaster.show(itemView.context, message)

    fun showMessage(@StringRes resId: Int) = showMessage(itemView.context.getString(resId))

    protected open fun setupObservers() {
        viewModel.messageString.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })

        viewModel.messageStringId.observe(this, Observer {
            it.data?.run { showMessage(this) }
        })
    }

    abstract fun setupView(view: View)

}