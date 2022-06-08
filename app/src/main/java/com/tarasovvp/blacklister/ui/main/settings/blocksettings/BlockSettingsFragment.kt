package com.tarasovvp.blacklister.ui.main.settings.blocksettings

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.databinding.FragmentBlockSettingsBinding
import com.tarasovvp.blacklister.extensions.isServiceRunning
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.ForegroundCallService

class BlockSettingsFragment : BaseFragment<FragmentBlockSettingsBinding, BlockSettingsViewModel>() {

    override fun getViewBinding() = FragmentBlockSettingsBinding.inflate(layoutInflater)

    override val viewModelClass = BlockSettingsViewModel::class.java

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

    override fun observeLiveData() {

    }
}