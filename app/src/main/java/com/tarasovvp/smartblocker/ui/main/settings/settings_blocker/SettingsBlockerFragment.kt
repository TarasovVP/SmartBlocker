package com.tarasovvp.smartblocker.ui.main.settings.settings_blocker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.FragmentSettingsBlockerBinding
import com.tarasovvp.smartblocker.extensions.isTrue
import com.tarasovvp.smartblocker.extensions.parcelable
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.extensions.setSafeOnClickListener
import com.tarasovvp.smartblocker.local.SharedPrefs
import com.tarasovvp.smartblocker.models.CountryCode
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsBlockerFragment :
    BaseFragment<FragmentSettingsBlockerBinding, SettingsBlockerViewModel>() {

    override var layoutId = R.layout.fragment_settings_blocker
    override val viewModelClass = SettingsBlockerViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSmartBlockerOnSettings()
        setBlockHiddenSettings()
        viewModel.getCountryCodeWithCountry(SharedPrefs.country)
    }

    private fun setSmartBlockerOnSettings() {
        binding?.apply {
            settingsBlockerSwitch.isChecked = SharedPrefs.smartBlockerTurnOff.not()
            settingsBlockerDescribe.text =
                getString(if (settingsBlockerSwitch.isChecked) R.string.settings_blocker_on else R.string.settings_blocker_off)
            settingsBlockerSwitch.setOnCheckedChangeListener { _, isChecked ->
                SharedPrefs.smartBlockerTurnOff = isChecked.not()
                settingsBlockerDescribe.text =
                    getString(if (isChecked) R.string.settings_blocker_on else R.string.settings_blocker_off)
                (activity as MainActivity).apply {
                    if (isChecked) {
                        startBlocker()
                    } else {
                        stopBlocker()
                    }
                }
            }
        }
    }

    private fun setBlockHiddenSettings() {
        binding?.apply {
            settingsBlockerHiddenSwitch.isChecked = SharedPrefs.blockHidden
            settingsBlockerHiddenDescribe.text =
                getString(if (settingsBlockerHiddenSwitch.isChecked) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)
            settingsBlockerHiddenSwitch.setSafeOnClickListener {
                viewModel.changeBlockHidden(settingsBlockerHiddenSwitch.isChecked.not())
            }
        }
    }

    private fun setCountryCodeSettings(countryCode: CountryCode) {
        setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
            bundle.parcelable<CountryCode>(COUNTRY_CODE)?.let {
                SharedPrefs.countryCode = it.countryCode
                SharedPrefs.country = it.country
                setCountryCodeSettings(it)
                (activity as? MainActivity)?.getAllData()
            }
        }
        binding?.apply {
            settingsBlockerCountry.text = countryCode.countryEmoji()
            settingsBlockerCountry.setSafeOnClickListener {
                findNavController().navigate(SettingsBlockerFragmentDirections.startCountryCodeSearchDialog())
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            successBlockHiddenLiveData.safeSingleObserve(viewLifecycleOwner) { blockHidden ->
                binding?.settingsBlockerHiddenDescribe?.text =
                    getString(if (blockHidden) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)
                binding?.settingsBlockerHiddenSwitch?.isChecked = blockHidden
                SharedPrefs.blockHidden = blockHidden
            }
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner) { error ->
                binding?.settingsBlockerHiddenSwitch?.isChecked =
                    binding?.settingsBlockerHiddenSwitch?.isChecked.isTrue().not()
                showMessage(error, true)
            }
            countryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCode ->
                setCountryCodeSettings(countryCode)
            }
        }
    }
}