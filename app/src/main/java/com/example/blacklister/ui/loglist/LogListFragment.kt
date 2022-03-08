package com.example.blacklister.ui.loglist

import com.example.blacklister.databinding.FragmentLogListBinding
import com.example.blacklister.ui.base.BaseFragment

class LogListFragment : BaseFragment<FragmentLogListBinding, LogListViewModel>() {

    override fun getViewBinding() = FragmentLogListBinding.inflate(layoutInflater)

    override val viewModelClass = LogListViewModel::class.java

    override fun observeLiveData() {

    }

}