package com.tarasovvp.smartblocker.presentation.main.settings.settingslanguage

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsLanguageBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_RU
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.APP_LANG_UK
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsLanguageFragment :
    BaseFragment<FragmentSettingsLanguageBinding, SettingsLanguageViewModel>() {
    override var layoutId = R.layout.fragment_settings_language
    override val viewModelClass = SettingsLanguageViewModel::class.java

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAppLanguage()
        binding?.settingsLanguagesRg?.setOnCheckedChangeListener { radioGroup, rbId ->
            if (radioGroup.findViewById<RadioButton>(rbId).isPressed.not()) return@setOnCheckedChangeListener
            val appLang =
                when (rbId) {
                    R.id.settings_languages_rb_uk -> APP_LANG_UK
                    R.id.settings_languages_rb_ru -> APP_LANG_RU
                    else -> Constants.APP_LANG_EN
                }
            viewModel.setAppLanguage(appLang)
            (activity as? MainActivity)?.apply {
                if (intent.getBooleanExtra(Constants.IS_INSTRUMENTAL_TEST, false).not()) {
                    recreate()
                }
            }
        }
    }

    override fun observeLiveData() {
        viewModel.appLanguageLiveData.safeSingleObserve(viewLifecycleOwner) { appLang ->
            val radioButtonId =
                when (appLang) {
                    APP_LANG_UK -> R.id.settings_languages_rb_uk
                    APP_LANG_RU -> R.id.settings_languages_rb_ru
                    else -> R.id.settings_languages_rb_en
                }
            binding?.settingsLanguagesRg?.check(radioButtonId)
        }
    }
}
