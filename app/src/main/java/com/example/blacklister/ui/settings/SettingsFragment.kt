package com.example.blacklister.ui.settings

import com.example.blacklister.databinding.FragmentSettingsBinding
import com.example.blacklister.ui.base.BaseFragment

class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>() {

    override fun getViewBinding() = FragmentSettingsBinding.inflate(layoutInflater)

    override val viewModelClass = SettingsViewModel::class.java

}