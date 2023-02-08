package com.tarasovvp.smartblocker.ui.main.settings.settings_privacy

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsPrivacyBinding
import com.tarasovvp.smartblocker.extensions.initWebView
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseBindingFragment

class SettingsPrivacyFragment :
    BaseBindingFragment<FragmentSettingsPrivacyBinding>() {

    override var layoutId = R.layout.fragment_settings_privacy

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setProgressVisibility(true)
        binding?.settingsPrivacyWebView?.initWebView(getString(R.string.privacy_policy)) {
            (activity as MainActivity).setProgressVisibility(false)
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).setProgressVisibility(false)
    }
}