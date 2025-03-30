package com.tarasovvp.smartblocker.presentation.main.settings.settingsprivacy

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsPrivacyBinding
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.utils.extensions.initWebView
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsPrivacyFragment :
    BaseFragment<FragmentSettingsPrivacyBinding, SettingsPrivacyPolicyViewModel>() {
    override var layoutId = R.layout.fragment_settings_privacy
    override val viewModelClass = SettingsPrivacyPolicyViewModel::class.java

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAppLanguage()
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).setProgressVisibility(false)
    }

    override fun observeLiveData() {
        with(viewModel) {
            viewModel.appLanguageLiveData.safeSingleObserve(viewLifecycleOwner) { appLang ->
                viewModel.getPrivacyPolicy(appLang)
            }
            privacyPolicyLiveData.safeSingleObserve(viewLifecycleOwner) { privacyPolicy ->
                (activity as? MainActivity)?.setProgressVisibility(true)
                binding?.settingsPrivacyWebView?.initWebView(privacyPolicy) {
                    (activity as? MainActivity)?.setProgressVisibility(false)
                }
            }
        }
    }
}
