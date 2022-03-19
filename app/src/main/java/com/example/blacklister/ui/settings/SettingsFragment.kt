package com.example.blacklister.ui.settings

import android.os.Bundle
import android.view.View
import com.example.blacklister.databinding.FragmentSettingsBinding
import com.example.blacklister.extensions.isServiceRunning
import com.example.blacklister.ui.MainActivity
import com.example.blacklister.ui.base.BaseFragment
import com.example.blacklister.utils.ForegroundCallService

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