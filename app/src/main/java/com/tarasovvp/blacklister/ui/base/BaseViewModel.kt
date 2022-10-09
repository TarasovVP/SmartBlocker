package com.tarasovvp.blacklister.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.blacklister.extensions.launchIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val exceptionLiveData = MutableLiveData<String>()
    val isProgressProcess = MediatorLiveData<Boolean>()
    val isMainProgressProcess = MediatorLiveData<Boolean>()
    val progressStatusLiveData = MutableLiveData<String>()

    fun showMainProgress() {
        isMainProgressProcess.postValue(true)
    }

    fun hideMainProgress() {
        isMainProgressProcess.postValue(false)
    }

    fun hideProgress() {
        isProgressProcess.postValue(false)
    }

    fun showProgress() {
        isProgressProcess.postValue(true)
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