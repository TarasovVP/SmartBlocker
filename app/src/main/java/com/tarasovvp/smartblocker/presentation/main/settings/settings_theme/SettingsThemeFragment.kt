package com.tarasovvp.smartblocker.presentation.main.settings.settings_theme

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsThemeBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve

class SettingsThemeFragment :
    BaseFragment<FragmentSettingsThemeBinding, SettingsThemeViewModel>() {

    override var layoutId = R.layout.fragment_settings_theme
    override val viewModelClass = SettingsThemeViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAppTheme()
        binding?.appTheme?.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.app_theme_day -> AppCompatDelegate.MODE_NIGHT_NO
                R.id.app_theme_night -> AppCompatDelegate.MODE_NIGHT_YES
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
            viewModel.setAppTheme(mode)
            (activity as MainActivity).apply {
                if (intent.getBooleanExtra(Constants.IS_INSTRUMENTAL_TEST,false).not()) {
                    recreate()
                }
            }
        }
    }

    override fun observeLiveData() {
        viewModel.appThemeLiveData.safeSingleObserve(viewLifecycleOwner) { appTheme ->
            val radioButtonId = when (appTheme) {
                AppCompatDelegate.MODE_NIGHT_YES ->R.id.app_theme_night
                AppCompatDelegate.MODE_NIGHT_NO -> R.id.app_theme_day
                else -> R.id.app_theme_auto
            }
            binding?.appTheme?.check(radioButtonId)
        }
    }
}