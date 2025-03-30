package com.tarasovvp.smartblocker.presentation.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tarasovvp.smartblocker.presentation.uimodels.MainProgress
import com.tarasovvp.smartblocker.utils.extensions.launchIO
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val exceptionLiveData = MutableLiveData<String>()
    val isProgressProcessLiveData = MutableLiveData<Boolean>()
    val isMainProgressProcessLiveData = MutableLiveData<Boolean>()
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

    protected open fun onError(
        throwable: Throwable,
        block: suspend CoroutineScope.() -> Unit,
    ) {
        hideProgress()
        throwable.printStackTrace()
        exceptionLiveData.postValue(throwable.localizedMessage)
    }
}
