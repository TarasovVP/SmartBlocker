package com.tarasovvp.blacklister.ui.main.settings.applanguage

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentAppLanguageBinding
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class AppLanguageFragment :
    BaseBindingFragment<FragmentAppLanguageBinding>() {

    override fun getViewBinding() = FragmentAppLanguageBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val radioButtonId = when (SharedPreferencesUtil.appLang) {
            Constants.APP_LANG_UA -> R.id.settings_languages_rb_ua
            Constants.APP_LANG_EN -> R.id.settings_languages_rb_en
            else -> R.id.settings_languages_rb_ru
        }
        binding?.settingsLanguagesRg?.check(radioButtonId)
        binding?.settingsLanguagesRg?.setOnCheckedChangeListener { _, rbId ->
            val appLang = when (rbId) {
                R.id.settings_languages_rb_ua -> Constants.APP_LANG_UA
                R.id.settings_languages_rb_en -> Constants.APP_LANG_EN
                else -> Constants.APP_LANG_RU
            }
            SharedPreferencesUtil.appLang = appLang
        }
        binding?.settingsLanguagesBtn?.setSafeOnClickListener {
            (activity as MainActivity).apply {
                recreate()
            }
        }
    }
}