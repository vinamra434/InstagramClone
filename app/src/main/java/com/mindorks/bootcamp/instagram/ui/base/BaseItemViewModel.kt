package com.mindorks.bootcamp.instagram.ui.base

import androidx.lifecycle.MutableLiveData
import com.mindorks.bootcamp.instagram.utils.network.NetworkHelper
import com.mindorks.bootcamp.instagram.utils.rx.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable

abstract class BaseItemViewModel<T : Any>(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {

    val itemViewData: MutableLiveData<T> = MutableLiveData()

    fun onManualCleared() = onCleared()

    /*call whenever need to update any data in the list eg. remove like field, added like field*/
    fun updateData(modifiedData: T) {
        itemViewData.postValue(modifiedData)
    }
}