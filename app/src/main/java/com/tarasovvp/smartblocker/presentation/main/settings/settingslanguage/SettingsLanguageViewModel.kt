package com.tarasovvp.smartblocker.presentation.main.settings.settingslanguage

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.tarasovvp.smartblocker.domain.usecases.SettingsLanguageUseCase
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsLanguageViewModel
    @Inject
    constructor(
        application: Application,
        private val settingsLanguageUseCase: SettingsLanguageUseCase,
    ) : BaseViewModel(application) {
        val appLanguageLiveData = MutableLiveData<String>()

        fun getAppLanguage() {
            launch {
                settingsLanguageUseCase.getAppLanguage().collect { appLang ->
                    appLanguageLiveData.postValue(appLang ?: Locale.getDefault().language)
                }
            }
        }

        fun setAppLanguage(appLang: String) {
            launch {
                settingsLanguageUseCase.setAppLanguage(appLang)
            }
        }
    }
