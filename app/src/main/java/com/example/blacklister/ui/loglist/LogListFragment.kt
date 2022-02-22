package com.example.blacklister.ui.loglist

import com.example.blacklister.databinding.LogListFragmentBinding
import com.example.blacklister.ui.base.BaseFragment

class LogListFragment : BaseFragment<LogListFragmentBinding, LogListViewModel>() {

    override fun getViewBinding() = LogListFragmentBinding.inflate(layoutInflater)

    override val viewModelClass = LogListViewModel::class.java

}