package com.example.blacklister.ui.numberlist

import com.example.blacklister.databinding.NumberListFragmentBinding
import com.example.blacklister.ui.base.BaseFragment

class NumberListFragment : BaseFragment<NumberListFragmentBinding, NumberListViewModel>() {

    override fun getViewBinding() = NumberListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = NumberListViewModel::class.java

}