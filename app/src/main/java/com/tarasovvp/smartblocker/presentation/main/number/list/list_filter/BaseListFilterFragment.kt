package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.models.database_views.FilterWithCountryCode
import com.tarasovvp.smartblocker.domain.models.entities.CountryCode
import com.tarasovvp.smartblocker.domain.models.entities.Filter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.databinding.FragmentListFilterBinding
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.models.InfoData
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseListFilterFragment :
    BaseListFragment<FragmentListFilterBinding, ListFilterViewModel, FilterWithCountryCode>() {

    override var layoutId = R.layout.fragment_list_filter
    override val viewModelClass = ListFilterViewModel::class.java

    private var filterWithCountryCodeList: ArrayList<FilterWithCountryCode>? = null
    private var isDeleteMode = false
    var filterIndexes: ArrayList<Int>? = null

    override fun createAdapter(): BaseAdapter<FilterWithCountryCode>? {
        return context?.let {
            FilterAdapter(object : FilterClickListener {
                override fun onFilterClick(filterWithCountryCode: FilterWithCountryCode) {
                    startNextScreen(filterWithCountryCode)
                }

                override fun onFilterLongClick() {
                    changeDeleteMode()
                }

                override fun onFilterDeleteCheckChange(filterWithCountryCode: FilterWithCountryCode) {
                    filterWithCountryCodeList?.find { it.filter == filterWithCountryCode.filter }?.filter?.isCheckedForDelete =
                        filterWithCountryCode.filter?.isCheckedForDelete.isTrue()
                    if (filterWithCountryCodeList?.any { it.filter?.isCheckedForDelete.isTrue() }.isNotTrue() && isDeleteMode) {
                        changeDeleteMode()
                    }
                }
            })
        }
    }

    override fun initViews() {
        binding?.apply {
            swipeRefresh = listFilterRefresh
            recyclerView = listFilterRecyclerView
            emptyStateContainer = listFilterEmpty
            listFilterRecyclerView.hideKeyboardWithLayoutTouch()
        }
    }

    override fun setFilterCheck() {
        binding?.listFilterCheck?.apply {
            isSelected = true
            text = context?.numberDataFilteringText(filterIndexes ?: arrayListOf())
            isChecked = filterIndexes.isNullOrEmpty().not()
            isEnabled = adapter?.itemCount.orZero() > 0 || filterIndexes.isNullOrEmpty().not()
        }
    }

    private fun setFilterListData(filterWithCountryCodeList: List<FilterWithCountryCode>) {
        binding?.listFilterCheck?.isEnabled = filterWithCountryCodeList.isNotEmpty() || binding?.listFilterCheck?.isChecked.isTrue()
        checkDataListEmptiness(filterWithCountryCodeList.isEmpty())
        viewModel.getHashMapFromFilterList(filterWithCountryCodeList, swipeRefresh?.isRefreshing.isTrue())
    }

    private fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.not()
        binding?.listFilterCheck?.isEnabled = isDeleteMode.not()
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
                setSearchViewMenu()
            }
        }
        binding?.apply {
            fabNew.isVisible = isDeleteMode.not()
            if (fabFull.isShown) {
                fabNew.performClick()
            }
        }
    }

    private fun startNextScreen(filterWithCountryCode: FilterWithCountryCode) {
        val direction = if (this is ListBlockerFragment) {
            if (filterWithCountryCode.filter?.filter.orEmpty().isEmpty()) {
                filterWithCountryCode.filter?.filterType = BLOCKER
                ListBlockerFragmentDirections.startCreateFilterFragment(
                    filterWithCountryCode = filterWithCountryCode
                )
            } else {
                ListBlockerFragmentDirections.startDetailsFilterFragment(
                    filterWithCountryCode = filterWithCountryCode
                )
            }
        } else {
            if (filterWithCountryCode.filter?.filter.orEmpty().isEmpty()) {
                filterWithCountryCode.filter?.filterType = PERMISSION
                ListPermissionFragmentDirections.startCreateFilterFragment(
                    filterWithCountryCode = filterWithCountryCode
                )
            } else {
                ListPermissionFragmentDirections.startDetailsFilterFragment(
                    filterWithCountryCode = filterWithCountryCode
                )
            }
        }
        findNavController().navigate(direction)
    }

    override fun setClickListeners() {
        binding?.apply {
            listFilterCheck.setSafeOnClickListener {
                root.hideKeyboard()
                listFilterCheck.isChecked =
                    listFilterCheck.isChecked.isTrue().not()
                findNavController().navigate(if (this@BaseListFilterFragment is ListBlockerFragment) {
                    ListBlockerFragmentDirections.startNumberDataFilteringDialog(
                        previousDestinationId = findNavController().currentDestination?.id.orZero(),
                        filteringList = filterIndexes.orEmpty().toIntArray()
                    )
                } else {
                    ListPermissionFragmentDirections.startNumberDataFilteringDialog(
                        previousDestinationId = findNavController().currentDestination?.id.orZero(),
                        filteringList = filterIndexes.orEmpty().toIntArray()
                    )
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
                startNextScreen(FilterWithCountryCode().apply {
                    filter = Filter(conditionType = FilterCondition.FILTER_CONDITION_FULL.index)
                    countryCode = CountryCode()
                })
            }
            fabStart.setSafeOnClickListener {
                startNextScreen(FilterWithCountryCode().apply {
                    filter = Filter(conditionType = FilterCondition.FILTER_CONDITION_START.index)
                    countryCode = CountryCode()
                })
            }
            fabContain.setSafeOnClickListener {
                startNextScreen(FilterWithCountryCode().apply {
                    filter = Filter(conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.index)
                    countryCode = CountryCode()
                })
            }
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(FILTER_ACTION) { _, _ ->
            val checkedFilterList = filterWithCountryCodeList?.filter { it.filter?.isCheckedForDelete.isTrue() }.orEmpty()
            val mappedFilterList = checkedFilterList.map { it.filter }
            viewModel.deleteFilterList(mappedFilterList)
        }
        setFragmentResultListener(FILTER_CONDITION_LIST) { _, bundle ->
            filterIndexes = bundle.getIntegerArrayList(FILTER_CONDITION_LIST)
            setFilterCheck()
            searchDataList()
        }
    }

    private fun setDeleteMenuClickListener() {
        (activity as MainActivity).toolbar?.apply {
            setOnMenuItemClickListener {
                val deleteFilterCount = filterWithCountryCodeList?.filter { it.filter?.isCheckedForDelete.isTrue() }.orEmpty().size
                val firstFilterWithCountryCode = filterWithCountryCodeList?.firstOrNull { it.filter?.isCheckedForDelete.isTrue() } as FilterWithCountryCode
                val filterWithCountryCode = firstFilterWithCountryCode.apply {
                    filter?.filterAction =
                        if (firstFilterWithCountryCode.filter?.isBlocker().isTrue()) FilterAction.FILTER_ACTION_BLOCKER_DELETE else FilterAction.FILTER_ACTION_PERMISSION_DELETE
                    filter?.filter =
                        if (deleteFilterCount > 1) resources.getQuantityString(R.plurals.list_filter_delete_amount,
                            deleteFilterCount.quantityString(),
                            deleteFilterCount) else filterWithCountryCodeList?.firstOrNull { it.filter?.isCheckedForDelete.isTrue() }?.filter?.filter.orEmpty()
                }
                val direction =
                    if (this@BaseListFilterFragment is ListBlockerFragment) {
                        ListBlockerFragmentDirections.startFilterActionDialog(filterWithCountryCode)
                    } else {
                        ListPermissionFragmentDirections.startFilterActionDialog(
                            filterWithCountryCode
                        )
                    }
                this@BaseListFilterFragment.findNavController().navigate(direction)
                true
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                if (this@BaseListFilterFragment.filterWithCountryCodeList == filterList) {
                    checkDataListEmptiness(filterList.isNullOrEmpty())
                    return@safeSingleObserve
                }
                this@BaseListFilterFragment.filterWithCountryCodeList = filterList as ArrayList<FilterWithCountryCode>
                searchDataList()
            }
            filteredFilterListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredFilterList ->
                setFilterListData(filteredFilterList)
            }
            filterHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filterList?.let { setDataList(it) }
            }
            successDeleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as MainActivity).apply {
                    showInterstitial()
                    getAllData()
                }
                changeDeleteMode()
            }
        }
    }

    override fun isFiltered(): Boolean {
        return filterIndexes.isNullOrEmpty().not()
    }

    override fun searchDataList() {
        (adapter as? FilterAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredFilterList(filterWithCountryCodeList.orEmpty(), searchQuery.orEmpty(), filterIndexes ?: arrayListOf())
    }

    override fun getData() {
        viewModel.getFilterList(this is ListBlockerFragment,
            swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        if (this@BaseListFilterFragment is ListBlockerFragment) {
            findNavController().navigate(
                ListBlockerFragmentDirections.startInfoFragment(
                    InfoData(
                        title = getString(Info.INFO_BLOCKER_LIST.title),
                        description = getString(Info.INFO_BLOCKER_LIST.description)
                    )
                )
            )
        } else {
            findNavController().navigate(
                ListPermissionFragmentDirections.startInfoFragment(
                    InfoData(
                        title = getString(Info.INFO_PERMISSION_LIST.title),
                        description = getString(Info.INFO_PERMISSION_LIST.description)
                    )
                )
            )
        }
    }
}

