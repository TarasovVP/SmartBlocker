package com.tarasovvp.smartblocker.ui.settings.settings_blocker

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.BlackListerApp
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
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            (activity as MainActivity).apply {
                settingsBlockerSwitch.isChecked = SharedPreferencesUtil.blockTurnOff.not()
                settingsBlockerSwitch.setOnCheckedChangeListener { _, isChecked ->
                    SharedPreferencesUtil.blockTurnOff = isChecked
                    (activity as MainActivity).apply {
                        if (isChecked.not()) {
                            startBlocker()
                        } else {
                            stopBlocker()
                        }
                    }
                }
            }
            settingsBlockerHiddenSwitch.isChecked = SharedPreferencesUtil.blockHidden
            settingsBlockerHiddenSwitch.setOnCheckedChangeListener { compoundButton, isChecked ->
                if (compoundButton.isPressed) {
                    if (BlackListerApp.instance?.isLoggedInUser()
                            .isTrue() && BlackListerApp.instance?.isNetworkAvailable.isNotTrue()
                    ) {
                        showMessage(getString(R.string.unavailable_network_repeat), true)
                    } else {
                        viewModel.changeBlockHidden(isChecked.not())
                    }
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                binding?.settingsBlockerHiddenSwitch?.isChecked = blockHidden.not()
                SharedPreferencesUtil.blockHidden = blockHidden
            }
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner) { error ->
                binding?.settingsBlockerHiddenSwitch?.isChecked = binding?.settingsBlockerHiddenSwitch?.isChecked.isTrue().not()
                showMessage(error, true)
            }
        }
    }
}