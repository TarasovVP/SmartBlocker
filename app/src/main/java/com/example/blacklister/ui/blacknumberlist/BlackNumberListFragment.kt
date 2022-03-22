package com.example.blacklister.ui.blacknumberlist

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.constants.Constants.BLACK_NUMBER
import com.example.blacklister.databinding.FragmentBlackNumberListBinding
import com.example.blacklister.extensions.hashMapFromList
import com.example.blacklister.model.BlackNumber
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.example.blacklister.utils.HeaderDataItem
import java.util.*

class BlackNumberListFragment :
    BaseListFragment<FragmentBlackNumberListBinding, BlackNumberListViewModel, BlackNumber>() {

    override fun getViewBinding() = FragmentBlackNumberListBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberListViewModel::class.java

    private var blackNumberList: List<BlackNumber>? = null

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

    override fun initView() {
        swipeRefresh = binding?.blackNumberListRefresh
        recyclerView = binding?.blackNumberListRecyclerView
        searchableEditText = binding?.blackNumberListSearch
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
                this@BlackNumberListFragment.blackNumberList = blackNumberList
                setBlackNumberList(blackNumberList)
            })
        }
    }

    private fun setBlackNumberList(blackNumberList: List<BlackNumber>) {
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
    }


    override fun filterDataList() {
        val filteredBlackNumberList = blackNumberList?.filter { blackNumber ->
            blackNumber.blackNumber.lowercase(Locale.getDefault()).contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            )
        } as ArrayList<BlackNumber>
        setBlackNumberList(filteredBlackNumberList)
    }

}