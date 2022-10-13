package com.tarasovvp.blacklister.ui.main.filter_list

import android.util.Log
import androidx.core.view.isInvisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.BlackListerApp
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.constants.Constants.BLACK_FILTER
import com.tarasovvp.blacklister.constants.Constants.WHITE_FILTER
import com.tarasovvp.blacklister.databinding.FragmentFilterListBinding
import com.tarasovvp.blacklister.enums.Condition
import com.tarasovvp.blacklister.extensions.filterDataList
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.Filter
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

open class BaseFilterListFragment :
    BaseListFragment<FragmentFilterListBinding, FilterListViewModel, Filter>() {

    override var layoutId = R.layout.fragment_filter_list
    override val viewModelClass = FilterListViewModel::class.java

    private var filterList: ArrayList<Filter>? = null
    private var isDeleteMode = false
    private var selectedFilterItems: ArrayList<Condition>? = null

    override fun createAdapter(): BaseAdapter<Filter>? {
        Log.e("adapterTAG", "FilterListFragment createAdapter filterList?.size ${filterList?.size}")
        return context?.let {
            FilterAdapter(object : FilterClickListener {
                override fun onFilterClick(filter: Filter) {
                    Log.e("filterLifeCycleTAG",
                        "FilterListFragment onFilterClick startFilterAddFragment")
                    startFilterAddScreen(filter)
                }

                override fun onFilterLongClick() {
                    changeDeleteMode()
                }

                override fun onFilterDeleteCheckChange(filter: Filter) {
                    filterList?.find { it.filter == filter.filter }?.isCheckedForDelete =
                        filter.isCheckedForDelete
                    if (filterList?.any { it.isCheckedForDelete }.isTrue().not() && isDeleteMode) {
                        changeDeleteMode()
                    }
                }
            })
        }
    }

    override fun initView() {
        findNavController().currentDestination?.label =
            getString(if (this@BaseFilterListFragment is BlackFilterListFragment) R.string.black_list else R.string.white_list)
        (activity as MainActivity).toolbar?.title = findNavController().currentDestination?.label
        selectedFilterItems = selectedFilterItems ?: ArrayList(Condition.values().toList())
        binding?.filterListFilter?.text =
            if (selectedFilterItems.orEmpty()
                    .any { it.isSelected }
            ) selectedFilterItems?.filter { it.isSelected }
                ?.joinToString { getString(it.title) } else getString(R.string.filter_no_filter)
        binding?.apply {
            swipeRefresh = filterListRefresh
            recyclerView = filterListRecyclerView
            emptyStateContainer = filterListEmpty
        }
        setClickListeners()
        setFragmentResultListener(Constants.DELETE_FILTER) { _, _ ->
            if (BlackListerApp.instance?.isLoggedInUser()
                    .isTrue() && BlackListerApp.instance?.isNetworkAvailable.isTrue().not()
            ) {
                showMessage(getString(R.string.unavailable_network_repeat), true)
            } else {
                viewModel.deleteFilterList(filterList?.filter { it.isCheckedForDelete }.orEmpty())
            }
        }
    }

    private fun setClickListeners() {
        binding?.filterListFilter?.setSafeOnClickListener {
            selectedFilterItems?.let { conditionList ->
                context?.filterDataList(conditionList) {
                    binding?.filterListFilter?.text =
                        if (selectedFilterItems.orEmpty()
                                .any { it.isSelected }
                        ) selectedFilterItems?.filter { it.isSelected }
                            ?.joinToString { getString(it.title) } else getString(R.string.filter_no_filter)
                    searchDataList()
                }
            }
        }
        binding?.filterListFabMenu?.setFabClickListener { conditionType ->
            startFilterAddScreen(Filter().apply {
                filterType =
                    if (this@BaseFilterListFragment is BlackFilterListFragment) BLACK_FILTER else WHITE_FILTER
                this.conditionType = conditionType
            })
        }
    }

    private fun startFilterAddScreen(filter: Filter) {
        val direction = if (this is BlackFilterListFragment) {
            BlackFilterListFragmentDirections.startFilterAddFragment(
                filter = filter)
        } else {
            WhiteFilterListFragmentDirections.startFilterAddFragment(
                filter = filter)
        }
        findNavController().navigate(direction)
    }

    private fun changeDeleteMode() {
        Log.e("destinationTAG", "FilterList changeDeleteMode isDeleteMode $isDeleteMode")
        isDeleteMode = isDeleteMode.not()
        (adapter as FilterAdapter).apply {
            isDeleteMode = this@BaseFilterListFragment.isDeleteMode
            notifyDataSetChanged()
        }
        findNavController().currentDestination?.label =
            if (isDeleteMode) getString(R.string.delete_) else getString(if (this@BaseFilterListFragment is BlackFilterListFragment) R.string.black_list else R.string.white_list)
        (activity as MainActivity).toolbar?.apply {
            title =
                if (isDeleteMode) getString(R.string.delete_) else getString(if (this@BaseFilterListFragment is BlackFilterListFragment) R.string.black_list else R.string.white_list)
            menu?.clear()
            if (isDeleteMode) {
                inflateMenu(R.menu.toolbar_delete)
                setDeleteMenuClickListener()
            } else {
                inflateMenu(R.menu.toolbar_search)
            }
        }
    }

    private fun setDeleteMenuClickListener() {
        (activity as MainActivity).toolbar?.apply {
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete_menu_item -> {
                        val direction =
                            if (this@BaseFilterListFragment is BlackFilterListFragment) {
                                BlackFilterListFragmentDirections.startDeleteFilterDialog()
                            } else {
                                WhiteFilterListFragmentDirections.startDeleteFilterDialog()
                            }
                        this@BaseFilterListFragment.findNavController().navigate(direction)
                        true
                    }
                    R.id.close_menu_item -> {
                        (adapter as FilterAdapter).apply {
                            isDeleteMode = false
                            filterList?.forEach {
                                it.isCheckedForDelete = false
                            }
                            notifyDataSetChanged()
                        }
                        true
                    }
                    else -> return@setOnMenuItemClickListener true
                }
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                this@BaseFilterListFragment.filterList = filterList as ArrayList<Filter>
                searchDataList()
            }
            filterHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filterList?.let { setDataList(it) }
            }
            successDeleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).getAllData()
                this@BaseFilterListFragment.filterList?.removeAll { it.isCheckedForDelete }
                changeDeleteMode()
                searchDataList()
            }
        }
    }

    override fun searchDataList() {
        (adapter as? FilterAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredList = filterList?.filter { filter ->
            filter.filter.lowercase(Locale.getDefault()).contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ) && (selectedFilterItems?.find { it.title == Condition.CONDITION_TYPE_FULL.title }?.isSelected.isTrue() && filter.isTypeFull()
                    || selectedFilterItems?.find { it.title == Condition.CONDITION_TYPE_START.title }?.isSelected.isTrue() && filter.isTypeStart()
                    || selectedFilterItems?.find { it.title == Condition.CONDITION_TYPE_CONTAIN.title }?.isSelected.isTrue() && filter.isTypeContain()
                    || selectedFilterItems.orEmpty().any { it.isSelected }.not())
        }.orEmpty()
        Log.e("adapterTAG",
            "FilterList searchDataList filteredList.size ${filteredList.size} selectedFilterItems ${selectedFilterItems?.joinToString()}")
        binding?.filterListFilter?.isInvisible =
            (filteredList.isEmpty() && selectedFilterItems.orEmpty().none { it.isSelected })
        checkDataListEmptiness(filteredList, selectedFilterItems.orEmpty().any { it.isSelected })
        if (filteredList.isNotEmpty()) {
            viewModel.getHashMapFromFilterList(filteredList)
        }
    }

    override fun getData() {
        viewModel.getFilterList(this is BlackFilterListFragment)
    }
}

