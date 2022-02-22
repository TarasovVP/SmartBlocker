package com.example.blacklister.ui.settings

import com.example.blacklister.databinding.SettingsFragmentBinding
import com.example.blacklister.ui.base.BaseFragment

class SettingsFragment : BaseFragment<SettingsFragmentBinding, SettingsViewModel>() {

    override fun getViewBinding() = SettingsFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = SettingsViewModel::class.java

}