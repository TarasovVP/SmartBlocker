package com.tarasovvp.smartblocker.ui.main.settings.settings_blocker

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.databinding.FragmentSettingsBlockerBinding
import com.tarasovvp.smartblocker.extensions.isNotTrue
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment

class SettingsBlockerFragment :
    BaseFragment<FragmentSettingsBlockerBinding, SettingsBlockerViewModel>() {

    override var layoutId = R.layout.fragment_settings_blocker
    override val viewModelClass = SettingsBlockerViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSmartBlockerOnSettings()
        setBlockHiddenSettings()
    }

    private fun setSmartBlockerOnSettings() {
        binding?.apply {
            settingsBlockerSwitch.isChecked = SharedPreferencesUtil.smartBlockerTurnOff.not()
            settingsBlockerDescribe.text =
                getString(if (settingsBlockerSwitch.isChecked) R.string.settings_blocker_on else R.string.settings_blocker_off)
            settingsBlockerSwitch.setOnCheckedChangeListener { _, isChecked ->
                SharedPreferencesUtil.smartBlockerTurnOff = isChecked.not()
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
            settingsBlockerHiddenSwitchContainer.setSafeOnClickListener {
                    viewModel.changeBlockHidden(settingsBlockerHiddenSwitch.isChecked.not())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                binding?.settingsBlockerHiddenDescribe?.text =
                    getString(if (blockHidden) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)
                binding?.settingsBlockerHiddenSwitch?.isChecked = blockHidden
                SharedPreferencesUtil.blockHidden = blockHidden
            }
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner) { error ->
                binding?.settingsBlockerHiddenSwitch?.isChecked =
                    binding?.settingsBlockerHiddenSwitch?.isChecked.isTrue().not()
                showMessage(error, true)
            }
        }
    }
}