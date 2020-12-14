package com.mindorks.bootcamp.instagram.ui.discardchanges

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.ui.base.BaseViewModel
import com.mindorks.bootcamp.instagram.utils.common.Event
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

class DiscardChangeViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val yesSelected = MutableLiveData<Event<Boolean>>()

    override fun onCreate() {}

    fun onYesClicked() {
        yesSelected.postValue(Event(true))
    }

}