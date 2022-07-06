package com.tarasovvp.blacklister.ui.main.settings.blocksettings

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.WHITE_LIST_PRIORITY
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.repository.WhiteNumberRepository
import com.tarasovvp.blacklister.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class BlockSettingsViewModel(application: Application) : BaseViewModel(application) {

    val isWhiteListPriorityLiveData = MutableLiveData<Boolean>()

    fun changePriority(whiteListPriority: Boolean) {
        viewModelScope.launch {
            try {
                if (BlackListerApp.instance?.isLoggedInUser().isTrue()) {
                    WhiteNumberRepository.database.child(Constants.USERS)
                        .child(FirebaseAuth.getInstance().currentUser?.uid.orEmpty())
                        .child(WHITE_LIST_PRIORITY).setValue(whiteListPriority)
                        .addOnCompleteListener {
                            isWhiteListPriorityLiveData.postValue(it.isSuccessful)
                        }
                } else {
                    SharedPreferencesUtil.isWhiteListPriority = whiteListPriority
                }
            } catch (e: Exception) {
                exceptionLiveData.postValue(e.localizedMessage)
            }
        }
    }
}