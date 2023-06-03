package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentSettingsBlockerBinding
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.parcelable
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsBlockerFragment :
    BaseFragment<FragmentSettingsBlockerBinding, SettingsBlockerViewModel>() {

    override var layoutId = R.layout.fragment_settings_blocker
    override val viewModelClass = SettingsBlockerViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeLiveData()
        viewModel.getBlockerTurnOn()
        viewModel.getBlockHidden()
        viewModel.getCurrentCountryCode()
    }

    private fun setBlockerTurnOnSettings(blockerTurnOn: Boolean) {
        binding?.apply {
            activity?.runOnUiThread {
                settingsBlockerSwitch.isChecked = blockerTurnOn
                settingsBlockerDescribe.text =
                    getString(if (blockerTurnOn) R.string.settings_blocker_on else R.string.settings_blocker_off )
            }
            settingsBlockerSwitch.setOnCheckedChangeListener { _, isChecked ->
                viewModel.setBlockerTurnOn(isChecked.not())
            }
        }
    }

    private fun setBlockHiddenSettings(blockHidden: Boolean) {
        binding?.apply {
            activity?.runOnUiThread {
                settingsBlockerHiddenSwitch.isChecked = blockHidden
                settingsBlockerHiddenDescribe.text =
                    getString(if (blockHidden) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)
            }
            settingsBlockerHiddenSwitch.setSafeOnClickListener {
                viewModel.changeBlockHidden(settingsBlockerHiddenSwitch.isChecked)
            }
        }
    }

    private fun setCountryCodeSettings(countryCode: CountryCodeUIModel) {
        setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
            bundle.parcelable<CountryCodeUIModel>(COUNTRY_CODE)?.let { currentCountryCode ->
                viewModel.setCurrentCountryCode(currentCountryCode)
                (activity as? MainActivity)?.getAllData()
            }
        }
        binding?.apply {
            activity?.runOnUiThread {
                settingsBlockerCountry.text = countryCode.countryEmoji()
                settingsBlockerCountry.setSafeOnClickListener {
                    findNavController().navigate(SettingsBlockerFragmentDirections.startCountryCodeSearchDialog())
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            blockerTurnOnLiveData.safeSingleObserve(viewLifecycleOwner) { blockerTurnOn ->
                setBlockerTurnOnSettings(blockerTurnOn)
                (activity as? MainActivity)?.apply {
                    if (blockerTurnOn) {
                        startBlocker()
                    } else {
                        stopBlocker()
                    }
                }
            }
            blockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                setBlockHiddenSettings(blockHidden)
            }
            currentCountryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { currentCountryCode ->
                setCountryCodeSettings(currentCountryCode)
            }
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                setBlockHidden(blockHidden)
            }
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner) { error ->
                binding?.settingsBlockerHiddenSwitch?.isChecked =
                    binding?.settingsBlockerHiddenSwitch?.isChecked.isTrue().not()
                showMessage(error, true)
            }
        }
    }
}