package com.tarasovvp.smartblocker.ui.number_data.list.list_filter

import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.SmartBlockerApp
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentListFilterBinding
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

open class BaseListFilterFragment :
    BaseListFragment<FragmentListFilterBinding, ListFilterViewModel, Filter>() {

    override var layoutId = R.layout.fragment_list_filter
    override val viewModelClass = ListFilterViewModel::class.java

    private var filterList: ArrayList<Filter>? = null
    private var isDeleteMode = false
    private var conditionFilterIndexes: ArrayList<Int>? = null

    override fun createAdapter(): BaseAdapter<Filter>? {
        return context?.let {
            FilterAdapter(object : FilterClickListener {
                override fun onFilterClick(filter: Filter) {
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
        findNavController().currentDestination?.label =
            getString(if (this@BaseListFilterFragment is ListBlockerFragment) R.string.list_blocker else R.string.list_permission)
        (activity as MainActivity).toolbar?.title = findNavController().currentDestination?.label
        binding?.apply {
            swipeRefresh = listFilterRefresh
            recyclerView = listFilterRecyclerView
            emptyStateContainer = listFilterEmpty
            listFilterRecyclerView.hideKeyboardWithLayoutTouch()
        }
        setFilterConditionFilter()
    }

    private fun setFilterConditionFilter() {
        conditionFilterIndexes = conditionFilterIndexes ?: arrayListOf()
        binding?.listFilterFilter?.apply {
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
        binding?.apply {
            listFilterFilter.setSafeOnClickListener {
                root.hideKeyboard()
                listFilterFilter.isChecked =
                    listFilterFilter.isChecked.isTrue().not()
                findNavController().navigate(if (this@BaseListFilterFragment is ListBlockerFragment) {
                    ListBlockerFragmentDirections.startFilterConditionsDialog(
                        filteringList = conditionFilterIndexes.orEmpty().toIntArray())
                } else {
                    ListPermissionFragmentDirections.startFilterConditionsDialog(
                        filteringList = conditionFilterIndexes.orEmpty().toIntArray())
                })
            }
            listFilterInfo.setSafeOnClickListener {
                showInfoScreen()
            }
            fabNew.setSafeOnClickListener {
                fabNew.setImageResource(if (fabFull.isVisible) R.drawable.ic_create else R.drawable.ic_close)
                if (fabFull.isVisible) fabFull.hide() else fabFull.show()
                if (fabStart.isVisible) fabStart.hide() else fabStart.show()
                if (fabContain.isVisible) fabContain.hide() else fabContain.show()
            }
            fabFull.setSafeOnClickListener {
                startNextScreen(Filter().apply {
                    filterType =
                        if (this@BaseListFilterFragment is ListBlockerFragment) BLOCKER else PERMISSION
                    this.conditionType = FilterCondition.FILTER_CONDITION_FULL.index
                })
            }
            fabStart.setSafeOnClickListener {
                startNextScreen(Filter().apply {
                    filterType =
                        if (this@BaseListFilterFragment is ListBlockerFragment) BLOCKER else PERMISSION
                    this.conditionType = FilterCondition.FILTER_CONDITION_START.index
                })
            }
            fabContain.setSafeOnClickListener {
                startNextScreen(Filter().apply {
                    filterType =
                        if (this@BaseListFilterFragment is ListBlockerFragment) BLOCKER else PERMISSION
                    this.conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index
                })
            }
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(FILTER_ACTION) { _, _ ->
            if (SmartBlockerApp.instance?.isLoggedInUser().isTrue()
                && SmartBlockerApp.instance?.isNetworkAvailable.isNotTrue()
            ) {
                showMessage(getString(R.string.app_network_unavailable_repeat), true)
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
        isDeleteMode = isDeleteMode.not()
        (adapter as FilterAdapter).apply {
            isDeleteMode = this@BaseListFilterFragment.isDeleteMode
            recyclerView?.post {
                adapter?.notifyDataSetChanged()
            }
        }
        (activity as MainActivity).toolbar?.apply {
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
                val direction =
                    if (this@BaseListFilterFragment is ListBlockerFragment) {
                        ListBlockerFragmentDirections.startFilterActionDialog(
                            filterAction = FilterAction.FILTER_ACTION_BLOCKER_DELETE,
                            filterNumber = String.format(getString(R.string.list_delete_amount),
                                filterList?.filter { it.isCheckedForDelete }
                                    .orEmpty().size))
                    } else {
                        ListPermissionFragmentDirections.startFilterActionDialog(
                            filterAction = FilterAction.FILTER_ACTION_PERMISSION_DELETE,
                            filterNumber = String.format(getString(R.string.list_delete_amount),
                                filterList?.filter { it.isCheckedForDelete }
                                    .orEmpty().size))
                    }
                this@BaseListFilterFragment.findNavController().navigate(direction)
                true
            }
        }
    }

    private fun startNextScreen(filter: Filter) {
        val direction = if (this is ListBlockerFragment) {
            if (filter.filter.isEmpty()) {
                ListBlockerFragmentDirections.startFilterAddFragment(
                    filterAdd = filter)
            } else {
                ListBlockerFragmentDirections.startFilterDetailFragment(
                    filterDetail = filter)
            }
        } else {
            if (filter.filter.isEmpty()) {
                ListPermissionFragmentDirections.startFilterAddFragment(
                    filterAdd = filter)
            } else {
                ListPermissionFragmentDirections.startFilterDetailFragment(
                    filterDetail = filter)
            }
        }
        findNavController().navigate(direction)
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                if (this@BaseListFilterFragment.filterList == filterList) {
                    checkDataListEmptiness(filterList.isNullOrEmpty())
                    return@safeSingleObserve
                }
                this@BaseListFilterFragment.filterList = filterList as ArrayList<Filter>
                searchDataList()
            }
            filterHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filterList?.let { setDataList(it) }
            }
            successDeleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).apply {
                    showInterstitial()
                    getAllData()
                }
                this@BaseListFilterFragment.filterList?.removeAll { it.isCheckedForDelete }
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
        binding?.listFilterFilter?.isEnabled =
            filteredList.isNotEmpty() || (filteredList.isEmpty() && conditionFilterIndexes.isNullOrEmpty()
                .not())
        checkDataListEmptiness(filteredList.isEmpty())
        if (filteredList.isNotEmpty()) {
            viewModel.getHashMapFromFilterList(filteredList, swipeRefresh?.isRefreshing.isTrue())
        }
    }

    override fun getData() {
        viewModel.getFilterList(this is ListBlockerFragment,
            swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        if (this is ListBlockerFragment) {
            findNavController().navigate(ListBlockerFragmentDirections.startInfoFragment(info = InfoData(
                title = getString(Info.INFO_BLOCKER_LIST.title),
                description = getString(Info.INFO_BLOCKER_LIST.description))))
        } else {
            findNavController().navigate(ListPermissionFragmentDirections.startInfoFragment(info = InfoData(
                title = getString(Info.INFO_PERMISSION_LIST.title),
                description = getString(Info.INFO_PERMISSION_LIST.description))))
        }
    }
}

