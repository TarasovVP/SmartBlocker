package com.tarasovvp.smartblocker.presentation.main.settings.settings_language

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.databinding.FragmentSettingsLanguageBinding
import com.tarasovvp.smartblocker.data.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseBindingFragment

class SettingsLanguageFragment :
    BaseBindingFragment<FragmentSettingsLanguageBinding>() {

    override var layoutId = R.layout.fragment_settings_language

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radioButtonId = when (SharedPrefs.appLang) {
            Constants.APP_LANG_UK -> R.id.settings_languages_rb_uk
            Constants.APP_LANG_RU -> R.id.settings_languages_rb_ru
            else -> R.id.settings_languages_rb_en
        }
        binding?.settingsLanguagesRg?.check(radioButtonId)
        binding?.settingsLanguagesRg?.setOnCheckedChangeListener { _, rbId ->
            val appLang = when (rbId) {
                R.id.settings_languages_rb_uk -> Constants.APP_LANG_UK
                R.id.settings_languages_rb_ru -> Constants.APP_LANG_RU
                else -> Constants.APP_LANG_EN
            }
            SharedPrefs.appLang = appLang
            (activity as MainActivity).apply {
                if (intent.getBooleanExtra(Constants.IS_INSTRUMENTAL_TEST,false).not()) {
                    recreate()
                }
            }
        }
    }
}