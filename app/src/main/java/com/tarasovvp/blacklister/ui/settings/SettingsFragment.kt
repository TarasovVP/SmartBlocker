package com.tarasovvp.blacklister.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.databinding.FragmentSettingsBinding
import com.tarasovvp.blacklister.extensions.isServiceRunning
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.ForegroundCallService
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

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
        binding?.settingsSignOutBtn?.setSafeOnClickListener {
            BlackListerApp.instance?.auth?.signOut()
            (activity as MainActivity).apply {
                finish()
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }

    override fun observeLiveData() {

    }

}