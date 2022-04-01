package com.tarasovvp.blacklister.ui.settings

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.databinding.FragmentSettingsBinding
import com.tarasovvp.blacklister.extensions.isServiceRunning
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.ForegroundCallService

class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>() {

    override fun getViewBinding() = FragmentSettingsBinding.inflate(layoutInflater)

    override val viewModelClass = SettingsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            binding?.settingsBackGroundCb?.isChecked =
                this.isServiceRunning(ForegroundCallService::class.java)
            binding?.settingsBackGroundCb?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    startService()
                } else {
                    stopService()
                }
            }
        }
    }

    override fun observeLiveData() {

    }

}