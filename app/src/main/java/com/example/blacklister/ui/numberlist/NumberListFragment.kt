package com.example.blacklister.ui.numberlist

import android.os.Bundle
import android.view.View
import com.example.blacklister.databinding.FragmentNumebrListBinding
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment

class NumberListFragment :
    BaseListFragment<FragmentNumebrListBinding, NumberListViewModel, BlackNumber>() {

    override fun getViewBinding() = FragmentNumebrListBinding.inflate(layoutInflater)

    override val viewModelClass = NumberListViewModel::class.java

    override fun createAdapter(): BaseAdapter<BlackNumber, *>? {
        return context?.let {
            BlackNumberAdapter(object : BlackNumberClickListener {
                override fun onBlackNumberClicked(blackNumber: BlackNumber) {

                }

            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.numberListRecyclerView?.initRecyclerView()
        viewModel.getBlackNumberList()
    }

    override fun observeLiveData() {
        with(viewModel) {
            blackNumberList.observe(viewLifecycleOwner, { blackNumberList ->
                onInitialDataLoaded(blackNumberList)
            })
        }
    }

}