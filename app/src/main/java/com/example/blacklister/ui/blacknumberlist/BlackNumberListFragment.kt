package com.example.blacklister.ui.blacknumberlist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.constants.Constants.BLACK_NUMBER
import com.example.blacklister.databinding.FragmentBlackNumebrListBinding
import com.example.blacklister.extensions.hashMapFromList
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.example.blacklister.utils.HeaderDataItem
import com.google.gson.Gson

class BlackNumberListFragment :
    BaseListFragment<FragmentBlackNumebrListBinding, BlackNumberListViewModel, BlackNumber>() {

    override fun getViewBinding() = FragmentBlackNumebrListBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberListViewModel::class.java

    override fun createAdapter(): BaseAdapter<BlackNumber>? {
        return context?.let {
            BlackNumberAdapter { blackNumber ->
                findNavController().navigate(
                    BlackNumberListFragmentDirections.startInfoDialog(
                        blackNumber = blackNumber
                    )
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh = binding?.blackNumberListRefresh
        binding?.blackNumberListRecyclerView?.initRecyclerView()
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
                val blackNumberHashMap = blackNumberList.hashMapFromList()
                for (blackNumberEntry in blackNumberHashMap) {
                    dataLoaded(
                        blackNumberEntry.value,
                        HeaderDataItem(
                            headerType = HeaderDataItem.HEADER_TYPE,
                            header = blackNumberEntry.key
                        )
                    )
                }
            })
        }
    }

}