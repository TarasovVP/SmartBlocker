package com.tarasovvp.smartblocker.ui.settings.settings_privacy

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsPrivacyBinding
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.ui.base.BaseFragment

class SettingsPrivacyViewModelFragment :
    BaseFragment<FragmentSettingsPrivacyBinding, SettingsPrivacyViewModel>() {

    override var layoutId = R.layout.fragment_settings_privacy
    override val viewModelClass = SettingsPrivacyViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {

    }


    override fun observeLiveData() {
        with(viewModel) {
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->

            }
        }
    }
}