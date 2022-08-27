package com.tarasovvp.blacklister.ui.settings.app_language

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentAppLanguageBinding
import com.tarasovvp.blacklister.extensions.showMessage
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AppLanguageFragment :
    BaseBindingFragment<FragmentAppLanguageBinding>() {

    override var layoutId = R.layout.fragment_app_language

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radioButtonId = when (SharedPreferencesUtil.appLang) {
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
            SharedPreferencesUtil.appLang = appLang
        }
        binding?.settingsLanguagesBtn?.setSafeOnClickListener {
            (activity as MainActivity).apply {
                recreate()
            }
            binding?.root?.showMessage(getString(R.string.success_), false)
        }
    }
}