package com.tarasovvp.smartblocker.presentation.main.settings.settings_theme

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatDelegate
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsThemeBinding
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsThemeFragment :
    BaseFragment<FragmentSettingsThemeBinding, SettingsThemeViewModel>() {
    override var layoutId = R.layout.fragment_settings_theme
    override val viewModelClass = SettingsThemeViewModel::class.java

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAppTheme()
        binding?.appThemeGroup?.setOnCheckedChangeListener { radioGroup, rbId ->
            if (radioGroup.findViewById<RadioButton>(rbId).isPressed.not()) return@setOnCheckedChangeListener
            val mode =
                when (rbId) {
                    R.id.app_theme_day -> AppCompatDelegate.MODE_NIGHT_NO
                    R.id.app_theme_night -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
            viewModel.setAppTheme(mode)
            activity?.recreate()
        }
    }

    override fun observeLiveData() {
        viewModel.appThemeLiveData.safeSingleObserve(viewLifecycleOwner) { appTheme ->
            val radioButtonId =
                when (appTheme) {
                    AppCompatDelegate.MODE_NIGHT_YES -> R.id.app_theme_night
                    AppCompatDelegate.MODE_NIGHT_NO -> R.id.app_theme_day
                    else -> R.id.app_theme_auto
                }
            activity?.runOnUiThread {
                binding?.appThemeGroup?.check(radioButtonId)
            }
        }
    }
}
