package com.tarasovvp.smartblocker.presentation.main.settings.settings_blocker

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.entities.db_entities.CountryCode
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.COUNTRY_CODE
import com.tarasovvp.smartblocker.databinding.FragmentSettingsBlockerBinding
import com.tarasovvp.smartblocker.data.prefs.SharedPrefs
import com.tarasovvp.smartblocker.presentation.mappers.CountryCodeUIMapper
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class SettingsBlockerFragment :
    BaseFragment<FragmentSettingsBlockerBinding, SettingsBlockerViewModel>() {

    @Inject
    lateinit var countryCodeUIMapper: CountryCodeUIMapper

    override var layoutId = R.layout.fragment_settings_blocker
    override val viewModelClass = SettingsBlockerViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSmartBlockerOnSettings()
        setBlockHiddenSettings()
        SharedPrefs.countryCode?.let { setCountryCodeSettings(it) }
    }

    private fun setSmartBlockerOnSettings() {
        binding?.apply {
            settingsBlockerSwitch.isChecked = SharedPrefs.smartBlockerTurnOff.isNotTrue()
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
            settingsBlockerHiddenSwitch.isChecked = SharedPrefs.blockHidden.isTrue()
            settingsBlockerHiddenDescribe.text =
                getString(if (settingsBlockerHiddenSwitch.isChecked) R.string.settings_block_hidden_on else R.string.settings_block_hidden_off)
            settingsBlockerHiddenSwitch.setSafeOnClickListener {
                viewModel.changeBlockHidden(settingsBlockerHiddenSwitch.isChecked)
            }
        }
    }

    private fun setCountryCodeSettings(countryCode: CountryCode) {
        setFragmentResultListener(COUNTRY_CODE) { _, bundle ->
            bundle.parcelable<CountryCode>(COUNTRY_CODE)?.let {
                SharedPrefs.countryCode = it
                setCountryCodeSettings(it)
                (activity as? MainActivity)?.getAllData()
            }
        }
        binding?.apply {
            settingsBlockerCountry.text = countryCodeUIMapper.mapToUIModel(countryCode).countryEmoji()
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
                Timber.e("SettingsBlockerFragment successBlockHiddenLiveData blockHidden $blockHidden")
            }
            exceptionLiveData.safeSingleObserve(viewLifecycleOwner) { error ->
                binding?.settingsBlockerHiddenSwitch?.isChecked =
                    binding?.settingsBlockerHiddenSwitch?.isChecked.isTrue().not()
                showMessage(error, true)
            }
        }
    }
}