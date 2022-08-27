package com.tarasovvp.blacklister.ui.settings.settings_list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentSettingsListBinding
import com.tarasovvp.blacklister.ui.base.BaseBindingFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener

class SettingsListFragment : BaseBindingFragment<FragmentSettingsListBinding>() {

    override var layoutId = R.layout.fragment_settings_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding?.settingsListBlockSettings?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startBlockSettingsFragment())
        }
        binding?.settingsListAccountDetails?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startAccountDetailsFragment())
        }
        binding?.settingsListAppLanguage?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startAppLanguageFragment())
        }
        binding?.settingsListAppTheme?.setSafeOnClickListener {
            findNavController().navigate(SettingsListFragmentDirections.startAppThemeFragment())
        }
    }

}