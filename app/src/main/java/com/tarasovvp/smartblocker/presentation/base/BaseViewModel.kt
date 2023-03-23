package com.tarasovvp.smartblocker.presentation.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.smartblocker.utils.extensions.launchIO
import com.tarasovvp.smartblocker.domain.models.MainProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val exceptionLiveData = MutableLiveData<String>()
    val isProgressProcessLiveData = MediatorLiveData<Boolean>()
    val isMainProgressProcessLiveData = MediatorLiveData<Boolean>()
    val progressStatusLiveData = MutableLiveData<MainProgress>()

    fun showProgress() {
        isProgressProcessLiveData.postValue(true)
    }

    fun hideProgress() {
        isProgressProcessLiveData.postValue(false)
    }

    protected fun launch(
        onError: (Throwable, suspend CoroutineScope.() -> Unit) -> Any? = ::onError,
        block: suspend CoroutineScope.() -> Unit,
    ): Job = viewModelScope.launchIO(onError, block)

    protected open fun onError(throwable: Throwable, block: suspend CoroutineScope.() -> Unit) {
        hideProgress()
        exceptionLiveData.postValue(throwable.localizedMessage)
    }
}