package com.tarasovvp.blacklister.ui.settings.block_settings

import android.os.Bundle
import android.view.View
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentBlockSettingsBinding
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseFragment

class BlockSettingsFragment : BaseFragment<FragmentBlockSettingsBinding, BlockSettingsViewModel>() {

    override var layoutId = R.layout.fragment_block_settings
    override val viewModelClass = BlockSettingsViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding?.apply {
            (activity as MainActivity).apply {
                blockSettingsTurn.setSwitchChange(SharedPreferencesUtil.blockTurnOff.not())
                checkBlockSettingsEnable(SharedPreferencesUtil.blockTurnOff.not())
                blockSettingsTurn.setSwitchClickListener { isChecked ->
                    blockSettingsTurn.setSwitchChange(isChecked.not())
                    SharedPreferencesUtil.blockTurnOff = isChecked
                    if (isChecked.not()) {
                        startBlocker()
                    } else {
                        stopBlocker()
                    }
                    checkBlockSettingsEnable(isChecked.not())
                }
                blockSettingsForeground.setSwitchChange(SharedPreferencesUtil.foreGround.not())
                blockSettingsForeground.setSwitchClickListener { isChecked ->
                    blockSettingsForeground.setSwitchChange(isChecked.not())
                    SharedPreferencesUtil.foreGround = isChecked
                    startBlocker()
                }
            }
            blockSettingsHidden.setSwitchChange(SharedPreferencesUtil.blockHidden)
            blockSettingsHidden.setSwitchClickListener { isChecked ->
                viewModel.changeBlockHidden(isChecked.not())
            }
            blockSettingsPriority.setSwitchChange(SharedPreferencesUtil.isWhiteListPriority)
            blockSettingsPriority.setSwitchClickListener { isChecked ->
                viewModel.changePriority(isChecked.not())
            }
        }
    }

    private fun checkBlockSettingsEnable(isChecked: Boolean) {
        binding?.apply {
            blockSettingsForeground.setEnableChange(isChecked)
            blockSettingsHidden.setEnableChange(isChecked)
            blockSettingsPriority.setEnableChange(isChecked)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successPriorityLiveData.safeSingleObserve(viewLifecycleOwner) { whiteListPriority ->
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