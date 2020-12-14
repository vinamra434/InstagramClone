package com.mindorks.bootcamp.instagram.ui.imageselection

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class ImageSelectionViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val onCameraSelected = MutableLiveData<Event<Boolean>>()
    val onGallerySelected = MutableLiveData<Event<Boolean>>()

    override fun onCreate() {
    }

    fun onCameraSelected() {
        onCameraSelected.postValue(Event(true))
    }

    fun onGallerySelected() {
        onGallerySelected.postValue(Event(true))
    }
}