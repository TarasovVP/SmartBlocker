package com.example.blacklister.ui.loglist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blacklister.R
import com.example.blacklister.databinding.LogListFragmentBinding
import com.example.blacklister.databinding.LoginFragmentBinding
import com.example.blacklister.ui.base.BaseBindingFragment

class LogListFragment : BaseBindingFragment<LogListFragmentBinding>() {

    private lateinit var viewModel: LogListViewModel

    override fun getViewBinding() = LogListFragmentBinding.inflate(layoutInflater)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.log_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(LogListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}