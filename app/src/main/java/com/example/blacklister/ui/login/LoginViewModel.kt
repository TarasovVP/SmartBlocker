package com.example.blacklister.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.blacklister.extensions.callLogList
import com.example.blacklister.extensions.contactList
import com.example.blacklister.extensions.toFormattedPhoneNumber
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.provider.BlackNumberRepositoryImpl
import com.example.blacklister.provider.CallLogRepositoryImpl
import com.example.blacklister.provider.ContactRepositoryImpl
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

}