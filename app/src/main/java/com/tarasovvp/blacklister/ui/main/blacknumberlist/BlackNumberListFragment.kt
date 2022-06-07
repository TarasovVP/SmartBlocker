package com.tarasovvp.blacklister.ui.main.blacknumberlist

import android.content.Context
import android.view.MenuItem
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_NUMBER
import com.tarasovvp.blacklister.databinding.FragmentBlackNumberListBinding
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.showPopUpMenu
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

class BlackNumberListFragment :
    BaseListFragment<FragmentBlackNumberListBinding, BlackNumberListViewModel, BlackNumber>() {

    override fun getViewBinding() = FragmentBlackNumberListBinding.inflate(layoutInflater)

    override val viewModelClass = BlackNumberListViewModel::class.java

    private var blackNumberList: List<BlackNumber>? = null

    override fun createAdapter(): BaseAdapter<BlackNumber>? {
        return context?.let { context ->
            BlackNumberAdapter { blackNumber, view ->
                val listener = PopupMenu.OnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.delete -> {
                            findNavController().navigate(BlackNumberListFragmentDirections.startInfoDialog(
                                blackNumber = blackNumber))
                        }
                        R.id.edit -> {
                            findNavController().navigate(BlackNumberListFragmentDirections.startBlackNumberAddFragment(
                                blackNumber))
                        }
                    }
                    true
                }
                context.showPopUpMenu(R.menu.black_number_menu, view, listener)
            }
        }
    }

    override fun initView() {
        swipeRefresh = binding?.blackNumberListRefresh
        recyclerView = binding?.blackNumberListRecyclerView
        searchableEditText = binding?.blackNumberListSearch
        emptyListText = binding?.blackNumberListEmpty
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BlackNumber>(
            BLACK_NUMBER
        )
            ?.safeSingleObserve(
                viewLifecycleOwner
            ) { blackNumber ->
                viewModel.deleteBlackNumber(blackNumber)
            }
        binding?.blackNumberListFabNew?.setSafeOnClickListener {
            findNavController().navigate(BlackNumberListFragmentDirections.startBlackNumberAddFragment())
        }
    }

    override fun getDataList() {
        viewModel.getBlackNumberList()
    }

    override fun observeLiveData() {
        with(viewModel) {
            blackNumberList.safeObserve(viewLifecycleOwner, { blackNumberList ->
                this@BlackNumberListFragment.blackNumberList = blackNumberList
                if (!checkDataListEmptiness(blackNumberList)) {
                    getHashMapFromBlackNumberList(blackNumberList)
                }
            })
            blackNumberHashMapLiveData.safeSingleObserve(viewLifecycleOwner, { blackNumberHashMap ->
                blackNumberHashMap?.let { setDataList(it) }
            })
        }
    }

    override fun searchDataList() {
        val filteredBlackNumberList = blackNumberList?.filter { blackNumber ->
            blackNumber.blackNumber.lowercase(Locale.getDefault()).contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            )
        } as ArrayList<BlackNumber>
        if (!checkDataListEmptiness(filteredBlackNumberList)) {
            viewModel.getHashMapFromBlackNumberList(filteredBlackNumberList)
        }
    }
}