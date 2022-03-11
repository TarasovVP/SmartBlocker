package com.example.blacklister.ui.loglist

import android.os.Bundle
import android.view.View
import com.example.blacklister.databinding.FragmentLogListBinding
import com.example.blacklister.ui.base.BaseFragment

class LogListFragment : BaseFragment<FragmentLogListBinding, LogListViewModel>() {

    override fun getViewBinding() = FragmentLogListBinding.inflate(layoutInflater)

    override val viewModelClass = LogListViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCallLogList()
    }

    override fun observeLiveData() {

    }

}