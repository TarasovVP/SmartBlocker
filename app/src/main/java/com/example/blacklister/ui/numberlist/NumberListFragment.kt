package com.example.blacklister.ui.numberlist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.constants.Constants.BLACK_NUMBER
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
                    findNavController().navigate(
                        NumberListFragmentDirections.startInfoDialog(
                            blackNumber = blackNumber
                        )
                    )
                }
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.numberListRecyclerView?.initRecyclerView()
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BlackNumber>(
            BLACK_NUMBER
        )
            ?.observe(
                viewLifecycleOwner
            ) { blackNumber ->
                blackNumber?.let {
                    viewModel.deleteBlackNumber(it)
                }
            }
    }

    override fun getDataList() {
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