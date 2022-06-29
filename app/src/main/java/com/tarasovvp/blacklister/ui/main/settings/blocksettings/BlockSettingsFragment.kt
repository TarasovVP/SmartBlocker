package com.tarasovvp.blacklister.ui.main.settings.blocksettings

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.databinding.FragmentBlockSettingsBinding
import com.tarasovvp.blacklister.extensions.isServiceRunning
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment
import com.tarasovvp.blacklister.utils.ForegroundCallService

class BlockSettingsFragment : BaseBindingFragment<FragmentBlockSettingsBinding>() {

    override fun getViewBinding() = FragmentBlockSettingsBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            binding?.blockSettingsBackGround?.isChecked =
                this.isServiceRunning(ForegroundCallService::class.java)
            binding?.blockSettingsBackGround?.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    startService()
                } else {
                    stopService()
                }
            }
        }
    }
}