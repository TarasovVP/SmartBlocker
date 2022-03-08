package com.example.blacklister.ui.numberlist

import com.example.blacklister.databinding.FragmentNumebrListBinding
import com.example.blacklister.ui.base.BaseFragment

class NumberListFragment : BaseFragment<FragmentNumebrListBinding, NumberListViewModel>() {

    override fun getViewBinding() = FragmentNumebrListBinding.inflate(layoutInflater)

    override val viewModelClass = NumberListViewModel::class.java

    override fun observeLiveData() {

    }

}