package com.example.blacklister.ui.registry

import com.example.blacklister.databinding.FragmentRegistryBinding
import com.example.blacklister.ui.base.BaseFragment

class RegistryFragment : BaseFragment<FragmentRegistryBinding, RegistryViewModel>() {

    override fun getViewBinding() = FragmentRegistryBinding.inflate(layoutInflater)

    override val viewModelClass = RegistryViewModel::class.java

}