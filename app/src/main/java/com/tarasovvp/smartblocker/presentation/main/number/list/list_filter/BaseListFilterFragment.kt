package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentListFilterBinding
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.ui_models.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.presentation.ui_models.InfoData
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class BaseListFilterFragment :
    BaseListFragment<FragmentListFilterBinding, ListFilterViewModel, FilterWithFilteredNumberUIModel>() {

    override var layoutId = R.layout.fragment_list_filter
    override val viewModelClass = ListFilterViewModel::class.java

    private var filterWithFilteredNumberUIModels: ArrayList<FilterWithFilteredNumberUIModel>? = null
    private var isDeleteMode = false
    private val filterWithCountryCodeUIModel by lazy { FilterWithCountryCodeUIModel() }

    override fun createAdapter(): BaseAdapter<FilterWithFilteredNumberUIModel>? {
        return context?.let {
            FilterAdapter(object : FilterClickListener {
                override fun onFilterClick(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
                    startDetailsFilterScreen(filterWithFilteredNumberUIModel)
                }

                override fun onFilterLongClick() {
                    changeDeleteMode()
                }

                override fun onFilterDeleteCheckChange(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
                    filterWithFilteredNumberUIModels?.find { it == filterWithFilteredNumberUIModel }?.isCheckedForDelete =
                        filterWithFilteredNumberUIModel.isCheckedForDelete.isTrue()
                    if (filterWithFilteredNumberUIModels?.any { it.isCheckedForDelete.isTrue() }.isNotTrue() && isDeleteMode) {
                        changeDeleteMode()
                    }
                }
            })
        }
    }

    private fun startDetailsFilterScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        val direction = if (this is ListBlockerFragment) {
            ListBlockerFragmentDirections.startDetailsFilterFragment(filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel)
        } else {
            ListPermissionFragmentDirections.startDetailsFilterFragment(filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel)
        }
        findNavController().navigate(direction)
    }

    override fun initViews() {
        binding?.apply {
            swipeRefresh = listFilterRefresh
            recyclerView = listFilterRecyclerView
            filterCheck = listFilterCheck
            emptyStateContainer = listFilterEmpty
            listFilterRecyclerView.hideKeyboardWithLayoutTouch()
        }
    }

    private fun setFilterListData(filterWithCountryCodeList: List<FilterWithFilteredNumberUIModel>) {
        binding?.listFilterCheck?.isEnabled = filterWithCountryCodeList.isNotEmpty() || binding?.listFilterCheck?.isChecked.isTrue()
        checkDataListEmptiness(filterWithCountryCodeList.isEmpty())
        setDataList(mapOf(String.EMPTY to filterWithCountryCodeList))
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
        (activity as? MainActivity)?.toolbar?.apply {
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

    override fun setClickListeners() {
        binding?.apply {
            listFilterCheck.setSafeOnClickListener {
                root.hideKeyboard()
                listFilterCheck.isChecked = listFilterCheck.isChecked.isTrue().not()
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
                filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.conditionType = FilterCondition.FILTER_CONDITION_FULL.ordinal
                viewModel.getCurrentCountryCode()
            }
            fabStart.setSafeOnClickListener {
                filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.conditionType = FilterCondition.FILTER_CONDITION_START.ordinal
                viewModel.getCurrentCountryCode()
            }
            fabContain.setSafeOnClickListener {
                filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.conditionType = FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
                filterWithCountryCodeUIModel.countryCodeUIModel = CountryCodeUIModel()
                startCreateFilterScreen()
            }
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(FILTER_ACTION) { _, _ ->
            val checkedFilterList = filterWithFilteredNumberUIModels?.filter { it.isCheckedForDelete.isTrue() }.orEmpty()
            viewModel.deleteFilterList(checkedFilterList)
        }
        setFragmentResultListener(FILTER_CONDITION_LIST) { _, bundle ->
            filterIndexes = bundle.getIntegerArrayList(FILTER_CONDITION_LIST)
            setFilterCheck()
            searchDataList()
        }
    }

    private fun startCreateFilterScreen() {
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filter = String.EMPTY
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filterType = if (this is ListBlockerFragment) BLOCKER else PERMISSION
        val direction = if (this is ListBlockerFragment) {
            ListBlockerFragmentDirections.startCreateFilterFragment(filterWithCountryCodeUIModel)
        } else {
            ListPermissionFragmentDirections.startCreateFilterFragment(filterWithCountryCodeUIModel)
        }
        findNavController().navigate(direction)
    }

    private fun setDeleteMenuClickListener() {
        (activity as? MainActivity)?.toolbar?.apply {
            setOnMenuItemClickListener {
                val deleteFilterCount = filterWithFilteredNumberUIModels?.filter { it.isCheckedForDelete }.orEmpty().size
                val firstFilterWithCountryCode = filterWithFilteredNumberUIModels?.firstOrNull { it.isCheckedForDelete } as FilterWithFilteredNumberUIModel
                val filterWithCountryCode = firstFilterWithCountryCode.apply {
                    filter =
                        if (deleteFilterCount > 1) resources.getQuantityString(R.plurals.list_filter_delete_amount,
                            deleteFilterCount.quantityString(),
                            deleteFilterCount) else filterWithFilteredNumberUIModels?.firstOrNull { it.isCheckedForDelete }?.filter.orEmpty()
                }
                val direction =
                    if (this@BaseListFilterFragment is ListBlockerFragment) {
                        ListBlockerFragmentDirections.startFilterActionDialog(filterWithCountryCode)
                    } else {
                        ListPermissionFragmentDirections.startFilterActionDialog(filterWithCountryCode)
                    }
                this@BaseListFilterFragment.findNavController().navigate(direction)
                true
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                this@BaseListFilterFragment.filterWithFilteredNumberUIModels = filterList as? ArrayList<FilterWithFilteredNumberUIModel>
                searchDataList()
            }
            filteredFilterListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredFilterList ->
                setFilterListData(filteredFilterList)
            }
            successDeleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as? MainActivity)?.apply {
                    showInterstitial()
                    getAllData()
                }
                changeDeleteMode()
            }
            currentCountryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeUIModel ->
                filterWithCountryCodeUIModel.countryCodeUIModel = countryCodeUIModel
                startCreateFilterScreen()
            }
        }
    }

    override fun isFiltered(): Boolean {
        return filterIndexes.isNullOrEmpty().not()
    }

    override fun searchDataList() {
        (adapter as? FilterAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredFilterList(filterWithFilteredNumberUIModels.orEmpty(), searchQuery.orEmpty(), filterIndexes ?: arrayListOf())
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
                        title = getString(Info.INFO_BLOCKER_LIST.title()),
                        description = getString(Info.INFO_BLOCKER_LIST.description())
                    )
                )
            )
        } else {
            findNavController().navigate(
                ListPermissionFragmentDirections.startInfoFragment(
                    InfoData(
                        title = getString(Info.INFO_PERMISSION_LIST.title()),
                        description = getString(Info.INFO_PERMISSION_LIST.description())
                    )
                )
            )
        }
    }
}

