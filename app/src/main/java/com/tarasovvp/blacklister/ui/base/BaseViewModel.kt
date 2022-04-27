package com.tarasovvp.blacklister.ui.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

open class BaseViewModel(application: Application) : AndroidViewModel(application) {

    val exceptionLiveData = MutableLiveData<String>()

}