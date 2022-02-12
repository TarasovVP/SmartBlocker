package com.example.blacklister.ui.numberlist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.blacklister.R
import com.example.blacklister.databinding.LogListFragmentBinding
import com.example.blacklister.databinding.NumberListFragmentBinding
import com.example.blacklister.ui.base.BaseBindingFragment

class NumberListFragment : BaseBindingFragment<NumberListFragmentBinding>() {

    private lateinit var viewModel: NumberListViewModel

    override fun getViewBinding() = NumberListFragmentBinding.inflate(layoutInflater)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.number_list_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NumberListViewModel::class.java)
        // TODO: Use the ViewModel
    }

}