package com.tarasovvp.smartblocker.ui.settings.settings_blocker

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsBlockerBinding
import com.tarasovvp.smartblocker.extensions.isNotTrue
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment

class SettingsBlockerFragment :
    BaseFragment<FragmentSettingsBlockerBinding, SettingsBlockerViewModel>() {

    override var layoutId = R.layout.fragment_settings_blocker
    override val viewModelClass = SettingsBlockerViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBlockerSettings()
        setBlockHiddenSettings()
    }

    private fun setBlockerSettings() {
        binding?.apply {
            settingsBlockerSwitch.isChecked = SharedPreferencesUtil.blockTurnOff.not()
            settingsBlockerDescribe.text =
                getString(if (settingsBlockerSwitch.isChecked) R.string.settings_blocker_on else R.string.settings_blocker_off)
            settingsBlockerSwitch.setOnCheckedChangeListener { _, isChecked ->
                SharedPreferencesUtil.blockTurnOff = isChecked
                settingsBlockerDescribe.text =
                    getString(if (isChecked) R.string.settings_blocker_on else R.string.settings_blocker_off)
                (activity as MainActivity).apply {
                    if (isChecked) {
                        startBlocker()
                    } else {
                        stopBlocker()
                    }
                }
            }
        }
    }

    private fun setBlockHiddenSettings() {
        binding?.apply {
            settingsBlockerHiddenSwitch.isChecked = SharedPreferencesUtil.blockHidden
            settingsBlockerHiddenDescribe.text =
                getString(if (settingsBlockerHiddenSwitch.isChecked) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)
            settingsBlockerHiddenSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
                settingsBlockerHiddenDescribe.text =
                    getString(if (isChecked) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)
                if (compoundButton.isPressed) {
                    if (SmartBlockerApp.instance?.isLoggedInUser()
                            .isTrue() && SmartBlockerApp.instance?.isNetworkAvailable.isNotTrue()
                    ) {
                        showMessage(getString(R.string.app_network_unavailable_repeat), true)
                    } else {
                        viewModel.changeBlockHidden(isChecked.not())
                        binding?.settingsBlockerHiddenSwitch?.isEnabled = false
                    }
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                binding?.settingsBlockerHiddenSwitch?.isEnabled = true
                binding?.settingsBlockerHiddenSwitch?.isChecked = blockHidden.not()
                SharedPreferencesUtil.blockHidden = blockHidden
            }
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner) { error ->
                binding?.settingsBlockerHiddenSwitch?.isEnabled = true
                binding?.settingsBlockerHiddenSwitch?.isChecked =
                    binding?.settingsBlockerHiddenSwitch?.isChecked.isTrue().not()
                showMessage(error, true)
            }
        }
    }
}