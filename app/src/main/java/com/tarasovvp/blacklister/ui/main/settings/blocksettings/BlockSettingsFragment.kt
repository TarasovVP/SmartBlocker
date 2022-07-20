package com.tarasovvp.blacklister.ui.main.settings.blocksettings

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.databinding.FragmentBlockSettingsBinding
import com.tarasovvp.blacklister.extensions.isServiceRunning
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment
import com.tarasovvp.blacklister.utils.ForegroundCallService

class BlockSettingsFragment : BaseFragment<FragmentBlockSettingsBinding, BlockSettingsViewModel>() {

    override fun getViewBinding() = FragmentBlockSettingsBinding.inflate(layoutInflater)
    override val viewModelClass = BlockSettingsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
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
        binding?.blockSettingsBackUnanimous?.isChecked = SharedPreferencesUtil.blockAnonymous
        binding?.blockSettingsBackUnanimous?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changeBlockAnonymous(isChecked)
        }
        binding?.blockSettingsPriority?.isChecked = SharedPreferencesUtil.isWhiteListPriority
        binding?.blockSettingsPriority?.setOnCheckedChangeListener { _, isChecked ->
            viewModel.changePriority(isChecked)
        }
    }

    override fun observeLiveData() = Unit
}