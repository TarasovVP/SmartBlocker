package com.tarasovvp.smartblocker.ui.settings.settings_theme

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentAppThemeBinding
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class SettingsThemeFragment :
    BaseBindingFragment<FragmentAppThemeBinding>() {

    override var layoutId = R.layout.fragment_app_theme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (SharedPreferencesUtil.appTheme) {
            AppCompatDelegate.MODE_NIGHT_YES -> binding?.appThemeNight?.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> binding?.appThemeDay?.isChecked = true
            else -> binding?.appAuto?.isChecked = true
        }
        binding?.appTheme?.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.app_theme_day -> {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
                R.id.app_theme_night -> {
                    AppCompatDelegate.MODE_NIGHT_YES
                }
                else -> {
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            }
            AppCompatDelegate.setDefaultNightMode(mode)
            SharedPreferencesUtil.appTheme = mode
        }
    }
}