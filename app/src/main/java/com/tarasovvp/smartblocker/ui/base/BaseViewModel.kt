package com.tarasovvp.smartblocker.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.smartblocker.extensions.launchIO
import com.tarasovvp.smartblocker.models.MainProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val exceptionLiveData = MutableLiveData<String>()
    val isProgressProcess = MediatorLiveData<Boolean>()
    val isMainProgressProcess = MediatorLiveData<Boolean>()
    val progressStatusLiveData = MutableLiveData<MainProgress>()

    fun showProgress() {
        isProgressProcess.postValue(true)
    }

    fun hideProgress() {
        isProgressProcess.postValue(false)
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