package com.example.blacklister.ui.registry

import com.example.blacklister.databinding.RegistryFragmentBinding
import com.example.blacklister.ui.base.BaseFragment

class RegistryFragment : BaseFragment<RegistryFragmentBinding, RegistryViewModel>() {

    override fun getViewBinding() = RegistryFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = RegistryViewModel::class.java

}