package com.tarasovvp.smartblocker.ui.main.settings.settings_theme

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsThemeBinding
import com.tarasovvp.smartblocker.local.SharedPrefs
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment
import javax.inject.Inject

class SettingsThemeFragment :
    BaseBindingFragment<FragmentSettingsThemeBinding>() {

    override var layoutId = R.layout.fragment_settings_theme

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when (SharedPrefs.appTheme) {
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
            SharedPrefs.appTheme = mode
            AppCompatDelegate.setDefaultNightMode(mode)
        }
    }
}