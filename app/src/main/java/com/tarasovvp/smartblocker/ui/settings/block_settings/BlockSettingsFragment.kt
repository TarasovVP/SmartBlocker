package com.tarasovvp.smartblocker.ui.settings.block_settings

import android.os.Bundle
import android.view.View
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentBlockSettingsBinding
import com.tarasovvp.smartblocker.extensions.isNotTrue
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.local.SharedPreferencesUtil
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment

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

    private fun checkBlockSettingsEnable(isChecked: Boolean) {
        binding?.apply {
            blockSettingsForeground.setEnableChange(isChecked)
            blockSettingsHidden.setEnableChange(isChecked)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                binding?.blockSettingsHidden?.setSwitchChange(blockHidden)
                SharedPreferencesUtil.blockHidden = blockHidden
                (activity as MainActivity).getAllData()
            }
        }
    }
}