package com.tarasovvp.blacklister.ui.registry

import com.tarasovvp.blacklister.databinding.FragmentRegistryBinding
import com.tarasovvp.blacklister.ui.base.BaseFragment

class RegistryFragment : BaseFragment<FragmentRegistryBinding, RegistryViewModel>() {

    override fun getViewBinding() = FragmentRegistryBinding.inflate(layoutInflater)

    override val viewModelClass = RegistryViewModel::class.java

    override fun observeLiveData() {

    }

}