package com.tarasovvp.blacklister.ui.main.filter_list

import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentFilterListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

open class FilterListFragment :
    BaseListFragment<FragmentFilterListBinding, FilterListViewModel, Filter>() {

    override var layoutId = R.layout.fragment_filter_list
    override val viewModelClass = FilterListViewModel::class.java

    private var filterList: ArrayList<Filter>? = null
    private var isDeleteMode = false
    private var selectedFilterItems: BooleanArray = booleanArrayOf(false, false, false)

    override fun createAdapter(): BaseAdapter<Filter>? {
        return context?.let {
            FilterAdapter(object : FilterClickListener {
                override fun onFilterClick(filter: Filter) {
                    findNavController().navigate(WhiteFilterListFragmentDirections.startAddFragment(
                        filter = filter))
                }

                override fun onFilterLongClick() {
                    changeDeleteMode()
                }

                override fun onFilterDeleteCheckChange(filter: Filter) {
                    filterList?.find { it.filter == filter.filter }?.isCheckedForDelete =
                        filter.isCheckedForDelete
                    if (filterList?.none { it.isCheckedForDelete }.isTrue()) {
                        changeDeleteMode()
                    }
                }
            })
        }
    }

    override fun initView() {
        swipeRefresh = binding?.filterListRefresh
        recyclerView = binding?.filterListRecyclerView
        emptyListText = binding?.filterListEmpty
        setClickListeners()
        setFragmentResultListener(Constants.DELETE_FILTER) { _, _ ->
            viewModel.deleteFilterList(filterList?.filter { it.isCheckedForDelete }.orEmpty(),
                this is BlackFilterListFragment)
        }
    }

    private fun setClickListeners() {
        binding?.filterListFabNew?.setSafeOnClickListener {
            val direction = if (this is BlackFilterListFragment) {
                BlackFilterListFragmentDirections.startAddFragment(
                    filter = Filter().apply {
                        isBlackFilter = true
                    })
            } else {
                WhiteFilterListFragmentDirections.startAddFragment(
                    filter = Filter().apply {
                        isBlackFilter = false
                    })
            }
            findNavController().navigate(direction)
        }
    }

    private fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.not()
        (adapter as FilterAdapter).apply {
            isDeleteMode = this@FilterListFragment.isDeleteMode
            notifyDataSetChanged()
        }
        binding?.apply {
            (activity as MainActivity).toolbar?.apply {
                Log.e("toolbarTAG", "FilterListFragment menu $menu")
                menu?.clear()
                inflateMenu(if (isDeleteMode) R.menu.toolbar_delete else R.menu.toolbar_search)
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.delete_menu_item -> {
                            val direction =
                                if (this@FilterListFragment is BlackFilterListFragment) {
                                    BlackFilterListFragmentDirections.startDeleteFilterDialog()
                                } else {
                                    WhiteFilterListFragmentDirections.startDeleteFilterDialog()
                                }
                            this@FilterListFragment.findNavController().navigate(direction)
                            true
                        }
                        else -> return@setOnMenuItemClickListener true
                    }
                }
            }

            if (isDeleteMode.not()) {
                filterList?.forEach {
                    it.isCheckedForDelete = false
                }
            }
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        Log.e("toolbarTAG",
            "FilterListFragment onPrepareOptionsMenu menu.getItem(0) ${menu.getItem(0)}")
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.e("toolbarTAG",
            "FilterListFragment onCreateOptionsMenu menu.getItem(0) ${menu.getItem(0)}")
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeObserve(viewLifecycleOwner) { filterList ->
                this@FilterListFragment.filterList = filterList as ArrayList<Filter>
                searchDataList()
            }
            filterHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filterList?.let { setDataList(it) }
            }
            successDeleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                this@FilterListFragment.filterList?.removeAll { it.isCheckedForDelete }
                changeDeleteMode()
                searchDataList()
            }
        }
    }

    override fun searchDataList() {
        val filteredList = filterList?.filter { filter ->
            filter.filter.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ) && (if (selectedFilterItems[0]) filter.contain else true)
                    && (if (selectedFilterItems[1]) filter.start else true)
                    && if (selectedFilterItems[2]) filter.end else true
        }.orEmpty()
        checkDataListEmptiness(filteredList)
        if (filteredList.isNotEmpty()) {
            viewModel.getHashMapFromFilterList(filteredList)
        }
    }

    override fun getData() {
        viewModel.getFilterList(this is BlackFilterListFragment)
    }
}

