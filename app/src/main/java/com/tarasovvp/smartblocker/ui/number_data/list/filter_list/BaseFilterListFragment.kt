package com.tarasovvp.smartblocker.ui.number_data.list.filter_list

import android.util.Log
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.BlackListerApp
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentFilterListBinding
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.FilterCondition
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.Filter
import com.tarasovvp.smartblocker.models.InfoData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.ui.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*

open class BaseFilterListFragment :
    BaseListFragment<FragmentFilterListBinding, FilterListViewModel, Filter>() {

    override var layoutId = R.layout.fragment_filter_list
    override val viewModelClass = FilterListViewModel::class.java

    private var filterList: ArrayList<Filter>? = null
    private var isDeleteMode = false
    private var conditionFilterIndexes: ArrayList<Int>? = null

    override fun createAdapter(): BaseAdapter<Filter>? {
        Log.e("adapterTAG", "FilterListFragment createAdapter filterList?.size ${filterList?.size}")
        return context?.let {
            FilterAdapter(object : FilterClickListener {
                override fun onFilterClick(filter: Filter) {
                    Log.e("filterLifeCycleTAG",
                        "FilterListFragment onFilterClick startFilterAddFragment")
                    startNextScreen(filter)
                }

                override fun onFilterLongClick() {
                    changeDeleteMode()
                }

                override fun onFilterDeleteCheckChange(filter: Filter) {
                    filterList?.find { it.filter == filter.filter }?.isCheckedForDelete =
                        filter.isCheckedForDelete
                    if (filterList?.any { it.isCheckedForDelete }.isNotTrue() && isDeleteMode) {
                        changeDeleteMode()
                    }
                }
            })
        }
    }

    override fun initViews() {
        Log.e("adapterTAG",
            "FilterListFragment initView selectedFilterItems $conditionFilterIndexes")
        findNavController().currentDestination?.label =
            getString(if (this@BaseFilterListFragment is BlockerListFragment) R.string.blocker_list else R.string.permission_list)
        (activity as MainActivity).toolbar?.title = findNavController().currentDestination?.label
        binding?.apply {
            swipeRefresh = filterListRefresh
            recyclerView = filterListRecyclerView
            emptyStateContainer = filterListEmpty
            filterListRecyclerView.hideKeyboardWithLayoutTouch()
        }
        setFilterConditionFilter()
    }

    private fun setFilterConditionFilter() {
        conditionFilterIndexes = conditionFilterIndexes ?: arrayListOf()
        binding?.filterListFilter?.apply {
            isSelected = true
            text =
                if (conditionFilterIndexes.isNullOrEmpty()) getString(R.string.filter_no_filter) else conditionFilterIndexes?.joinToString {
                    getString(FilterCondition.getTitleByIndex(it))
                }
            isChecked = conditionFilterIndexes.isNullOrEmpty().not()
            isEnabled =
                adapter?.itemCount.orZero() > 0 || conditionFilterIndexes.isNullOrEmpty().not()
        }
    }

    override fun setClickListeners() {
        binding?.filterListFilter?.setSafeOnClickListener {
            binding?.root?.hideKeyboard()
            binding?.filterListFilter?.isChecked =
                binding?.filterListFilter?.isChecked.isTrue().not()
            findNavController().navigate(if (this is BlockerListFragment) {
                BlockerListFragmentDirections.startFilterConditionsDialog(
                    filteringList = conditionFilterIndexes.orEmpty().toIntArray())
            } else {
                BlockerListFragmentDirections.startFilterConditionsDialog(
                    filteringList = conditionFilterIndexes.orEmpty().toIntArray())
            })
        }
        binding?.filterListInfo?.setSafeOnClickListener {
            showInfoScreen()
        }
        binding?.filterListFabMenu?.setFabClickListener { conditionType ->
            startNextScreen(Filter().apply {
                filterType =
                    if (this@BaseFilterListFragment is BlockerListFragment) BLOCKER else PERMISSION
                this.conditionType = conditionType
            })
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(FILTER_ACTION) { _, _ ->
            if (BlackListerApp.instance?.isLoggedInUser().isTrue()
                && BlackListerApp.instance?.isNetworkAvailable.isNotTrue()
            ) {
                showMessage(getString(R.string.unavailable_network_repeat), true)
            } else {
                viewModel.deleteFilterList(filterList?.filter { it.isCheckedForDelete }.orEmpty())
            }
        }
        setFragmentResultListener(FILTER_CONDITION_LIST) { _, bundle ->
            conditionFilterIndexes = bundle.getIntegerArrayList(FILTER_CONDITION_LIST)
            setFilterConditionFilter()
            searchDataList()
        }
    }

    private fun changeDeleteMode() {
        Log.e("destinationTAG", "FilterList changeDeleteMode isDeleteMode $isDeleteMode")
        isDeleteMode = isDeleteMode.not()
        (adapter as FilterAdapter).apply {
            isDeleteMode = this@BaseFilterListFragment.isDeleteMode
            recyclerView?.post {
                adapter?.notifyDataSetChanged()
            }
        }
        findNavController().currentDestination?.label =
            if (isDeleteMode) getString(R.string.delete_) else getString(if (this@BaseFilterListFragment is BlockerListFragment) R.string.blocker_list else R.string.permission_list)
        (activity as MainActivity).toolbar?.apply {
            title =
                if (isDeleteMode) getString(R.string.delete_) else getString(if (this@BaseFilterListFragment is BlockerListFragment) R.string.blocker_list else R.string.permission_list)
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
                        // TODO add filter number
                        val direction =
                            if (this@BaseFilterListFragment is BlockerListFragment) {
                                BlockerListFragmentDirections.startFilterActionDialog(
                                    filterNumber = "",
                                    filterAction = FilterAction.FILTER_ACTION_BLOCKER_DELETE)
                            } else {
                                BlockerListFragmentDirections.startFilterActionDialog(
                                    filterNumber = "",
                                    filterAction = FilterAction.FILTER_ACTION_PERMISSION_DELETE)
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

    private fun startNextScreen(filter: Filter) {
        val direction = if (this is BlockerListFragment) {
            if (filter.filter.isEmpty()) {
                BlockerListFragmentDirections.startFilterAddFragment(
                    filterAdd = filter)
            } else {
                BlockerListFragmentDirections.startFilterDetailFragment(
                    filterDetail = filter)
            }
        } else {
            if (filter.filter.isEmpty()) {
                BlockerListFragmentDirections.startFilterAddFragment(
                    filterAdd = filter)
            } else {
                BlockerListFragmentDirections.startFilterDetailFragment(
                    filterDetail = filter)
            }
        }
        findNavController().navigate(direction)
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                if (this@BaseFilterListFragment.filterList == filterList) {
                    checkDataListEmptiness(filterList.isNullOrEmpty())
                    return@safeSingleObserve
                }
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

    override fun isFiltered(): Boolean {
        return conditionFilterIndexes.isNullOrEmpty().not()
    }

    override fun searchDataList() {
        (adapter as? FilterAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredList = filterList?.filter { filter ->
            filter.filter.lowercase(Locale.getDefault())
                .contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty())
                    && (conditionFilterIndexes?.contains(FilterCondition.FILTER_CONDITION_FULL.index)
                .isTrue()
                    && filter.isTypeFull()
                    || conditionFilterIndexes?.contains(FilterCondition.FILTER_CONDITION_START.index)
                .isTrue()
                    && filter.isTypeStart()
                    || conditionFilterIndexes?.contains(FilterCondition.FILTER_CONDITION_CONTAIN.index)
                .isTrue()
                    && filter.isTypeContain()
                    || conditionFilterIndexes.isNullOrEmpty())
        }.orEmpty()
        Log.e("adapterTAG",
            "FilterList searchDataList filteredList.size ${filteredList.size} selectedFilterItems ${conditionFilterIndexes?.joinToString()}")
        binding?.filterListFilter?.isEnabled =
            filteredList.isNotEmpty() || (filteredList.isEmpty() && conditionFilterIndexes.isNullOrEmpty()
                .not())
        checkDataListEmptiness(filteredList.isEmpty())
        if (filteredList.isNotEmpty()) {
            viewModel.getHashMapFromFilterList(filteredList, swipeRefresh?.isRefreshing.isTrue())
        }
    }

    override fun getData() {
        Log.e("adapterTAG",
            "FilterList getData() filterList.size ${filterList?.size} selectedFilterItems ${conditionFilterIndexes?.joinToString()}")
        viewModel.getFilterList(this is BlockerListFragment,
            swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        if (this is BlockerListFragment) {
            findNavController().navigate(BlockerListFragmentDirections.startInfoFragment(info = InfoData(
                title = getString(Info.INFO_BLOCKER_LIST.title),
                description = getString(Info.INFO_CONTACT_LIST.description))))
        } else {
            findNavController().navigate(PermissionListFragmentDirections.startInfoFragment(info = InfoData(
                title = getString(Info.INFO_PERMISSION_LIST.title),
                description = getString(Info.INFO_CONTACT_LIST.description))))
        }
    }
}
