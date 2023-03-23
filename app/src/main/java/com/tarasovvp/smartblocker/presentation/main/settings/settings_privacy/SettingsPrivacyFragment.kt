package com.tarasovvp.smartblocker.presentation.main.settings.settings_privacy

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsPrivacyBinding
import com.tarasovvp.smartblocker.utils.extensions.initWebView
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseBindingFragment

class SettingsPrivacyFragment :
    BaseBindingFragment<FragmentSettingsPrivacyBinding>() {

    override var layoutId = R.layout.fragment_settings_privacy

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            setProgressVisibility(true)
            binding?.settingsPrivacyWebView?.initWebView(getString(R.string.privacy_policy)) {
                setProgressVisibility(false)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).setProgressVisibility(false)
    }
}