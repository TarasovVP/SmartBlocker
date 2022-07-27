package com.tarasovvp.blacklister.ui.settings.blocksettings

import android.os.Bundle
import android.util.Log
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
        binding?.apply {
            (activity as MainActivity).apply {
                blockSettingsTurn.setSwitchChange(this.isServiceRunning(ForegroundCallService::class.java))
                blockSettingsTurn.setClickListener { isChecked ->
                    blockSettingsTurn.setSwitchChange(isChecked.not())
                    if (isChecked) {
                        startService()
                    } else {
                        stopService()
                    }
                }
            }
            blockSettingsHidden.setSwitchChange(SharedPreferencesUtil.blockHidden)
            blockSettingsHidden.setClickListener { isChecked ->
                viewModel.changeBlockHidden(isChecked.not())
            }
            blockSettingsPriority.setSwitchChange(SharedPreferencesUtil.isWhiteListPriority)
            blockSettingsPriority.setClickListener { isChecked ->
                viewModel.changePriority(isChecked.not())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successPriorityLiveData.safeSingleObserve(viewLifecycleOwner) { whiteListPriority ->
                Log.e("settingsTAG", "BlockSettingsFragment successPriorityLiveData whiteListPriority $whiteListPriority")
                binding?.blockSettingsPriority?.setSwitchChange(whiteListPriority)
                SharedPreferencesUtil.isWhiteListPriority = whiteListPriority
                (activity as MainActivity).getAllData()
            }
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                binding?.blockSettingsHidden?.setSwitchChange(blockHidden)
                SharedPreferencesUtil.blockHidden = blockHidden
                (activity as MainActivity).getAllData()
            }
        }
    }
}