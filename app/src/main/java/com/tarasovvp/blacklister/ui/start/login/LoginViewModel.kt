package com.tarasovvp.blacklister.ui.start.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.GoogleAuthProvider
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.ui.start.GoogleLoginViewModel
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : GoogleLoginViewModel(application) {

}