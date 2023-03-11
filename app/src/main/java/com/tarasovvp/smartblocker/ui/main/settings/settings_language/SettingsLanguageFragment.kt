package com.tarasovvp.smartblocker.ui.main.settings.settings_language

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.APP_LANG_RU
import com.tarasovvp.smartblocker.constants.Constants.APP_LANG_UK
import com.tarasovvp.smartblocker.databinding.FragmentSettingsLanguageBinding
import com.tarasovvp.smartblocker.local.DataStorePrefs
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsLanguageFragment :
    BaseBindingFragment<FragmentSettingsLanguageBinding>() {

    override var layoutId = R.layout.fragment_settings_language

    @Inject
    lateinit var dataStorePrefs: DataStorePrefs

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            val radioButtonId = when (dataStorePrefs.getAppLang().first()) {
                APP_LANG_UK -> R.id.settings_languages_rb_uk
                APP_LANG_RU -> R.id.settings_languages_rb_ru
                else -> R.id.settings_languages_rb_en
            }
            binding?.settingsLanguagesRg?.check(radioButtonId)
        }

        binding?.settingsLanguagesRg?.setOnCheckedChangeListener { _, rbId ->
            val appLang = when (rbId) {
                R.id.settings_languages_rb_uk -> APP_LANG_UK
                R.id.settings_languages_rb_ru -> APP_LANG_RU
                else -> Constants.APP_LANG_EN
            }
            lifecycleScope.launch {
                dataStorePrefs.saveAppLang(appLang)
            }
            (activity as MainActivity).apply {
                recreate()
            }
        }
    }
}