package com.tarasovvp.smartblocker.presentation.main.number.list.listfilter

import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentListFilterBinding
import com.tarasovvp.smartblocker.domain.enums.FilterCondition
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.FILTER_CONDITION_LIST
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IS_DELETE_MODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LIST_STATE
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.presentation.base.BaseViewModel
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.uimodels.CountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithCountryCodeUIModel
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.hideKeyboard
import com.tarasovvp.smartblocker.utils.extensions.hideKeyboardWithLayoutTouch
import com.tarasovvp.smartblocker.utils.extensions.isNotTrue
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.orZero
import com.tarasovvp.smartblocker.utils.extensions.restoreListInstantState
import com.tarasovvp.smartblocker.utils.extensions.setSafeOnClickListener

abstract class BaseListFilterFragment<T : BaseViewModel> :
    BaseListFragment<FragmentListFilterBinding, T, FilterWithFilteredNumberUIModel>() {
    override var layoutId = R.layout.fragment_list_filter

    abstract fun getCurrentCountryCode()

    abstract fun deleteFilterList(checkedFilterList: List<FilterWithFilteredNumberUIModel>)

    abstract fun startDetailsFilterScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel)

    abstract fun startCreateFilterScreen()

    abstract fun startFilterActionScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel)

    protected var isDeleteMode = false

    protected var filterWithFilteredNumberUIModels: ArrayList<FilterWithFilteredNumberUIModel>? =
        null
    protected val filterWithCountryCodeUIModel by lazy { FilterWithCountryCodeUIModel() }

    override fun createAdapter(): BaseAdapter<FilterWithFilteredNumberUIModel>? {
        return context?.let {
            FilterAdapter(
                object : FilterClickListener {
                    override fun onFilterClick(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
                        startDetailsFilterScreen(filterWithFilteredNumberUIModel)
                    }

                    override fun onFilterLongClick() {
                        changeDeleteMode()
                    }

                    override fun onFilterDeleteCheckChange(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
                        filterWithFilteredNumberUIModels?.find { it == filterWithFilteredNumberUIModel }?.isCheckedForDelete =
                            filterWithFilteredNumberUIModel.isCheckedForDelete.isTrue()
                        if (filterWithFilteredNumberUIModels?.any { it.isCheckedForDelete.isTrue() }
                                .isNotTrue() && isDeleteMode
                        ) {
                            changeDeleteMode()
                        }
                    }
                },
            )
        }
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

    protected fun setFilterListData(filterWithCountryCodeList: List<FilterWithFilteredNumberUIModel>) {
        binding?.listFilterCheck?.isEnabled =
            filterWithCountryCodeList.isNotEmpty() || binding?.listFilterCheck?.isChecked.isTrue()
        checkDataListEmptiness(filterWithCountryCodeList.isEmpty())
        setDataList(mapOf(String.EMPTY to filterWithCountryCodeList))
        val savedStateHandle =
            when (this) {
                is ListBlockerFragment -> viewModel.savedStateHandle
                is ListPermissionFragment -> viewModel.savedStateHandle
                else -> null
            }
        savedStateHandle?.restoreListInstantState(LIST_STATE, recyclerView?.layoutManager)
        savedStateHandle?.get<Boolean>(IS_DELETE_MODE)?.apply {
            isDeleteMode = this.not()
            savedStateHandle[IS_DELETE_MODE] = null
            changeDeleteMode()
        }
    }

    private fun setDeleteMenuClickListener() {
        (activity as? MainActivity)?.toolbar?.apply {
            setOnMenuItemClickListener {
                val deleteFilterCount =
                    filterWithFilteredNumberUIModels?.filter { it.isCheckedForDelete }
                        .orEmpty().size
                val firstFilterWithCountryCode =
                    filterWithFilteredNumberUIModels?.firstOrNull { it.isCheckedForDelete }
                val filterWithFilteredNumberUIModel =
                    FilterWithFilteredNumberUIModel(
                        filter =
                            if (deleteFilterCount > 1) {
                                resources.getQuantityString(
                                    R.plurals.list_filter_delete_amount,
                                    deleteFilterCount,
                                    deleteFilterCount,
                                )
                            } else {
                                firstFilterWithCountryCode?.filter.orEmpty()
                            },
                        filterType = firstFilterWithCountryCode?.filterType.orZero(),
                        conditionType = firstFilterWithCountryCode?.conditionType.orZero(),
                    )
                startFilterActionScreen(filterWithFilteredNumberUIModel)
                true
            }
        }
    }

    protected fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.not()
        binding?.listFilterCheck?.isEnabled =
            isDeleteMode.not() && filterWithFilteredNumberUIModels.isNullOrEmpty().not()
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
                inflateMenu(R.menu.toolbar_search)
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
                findNavController().navigate(
                    if (this@BaseListFilterFragment is ListBlockerFragment) {
                        ListBlockerFragmentDirections.startNumberDataFilteringDialog(
                            previousDestinationId = findNavController().currentDestination?.id.orZero(),
                            filteringList = filterIndexes.orEmpty().toIntArray(),
                        )
                    } else {
                        ListPermissionFragmentDirections.startNumberDataFilteringDialog(
                            previousDestinationId = findNavController().currentDestination?.id.orZero(),
                            filteringList = filterIndexes.orEmpty().toIntArray(),
                        )
                    },
                )
            }
            listFilterInfo.setSafeOnClickListener {
                showInfoScreen()
            }
            fabNew.setSafeOnClickListener {
                fabNew.setImageResource(if (fabFull.isVisible) R.drawable.ic_create else R.drawable.ic_close)
                changeBackgroundWithFABShow(fabFull.isVisible)
                if (fabFull.isVisible) fabFull.hide() else fabFull.show()
                if (fabStart.isVisible) fabStart.hide() else fabStart.show()
                if (fabContain.isVisible) fabContain.hide() else fabContain.show()
            }
            fabFull.setSafeOnClickListener {
                filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.conditionType =
                    FilterCondition.FILTER_CONDITION_FULL.ordinal
                getCurrentCountryCode()
            }
            fabStart.setSafeOnClickListener {
                filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.conditionType =
                    FilterCondition.FILTER_CONDITION_START.ordinal
                getCurrentCountryCode()
            }
            fabContain.setSafeOnClickListener {
                filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.conditionType =
                    FilterCondition.FILTER_CONDITION_CONTAIN.ordinal
                filterWithCountryCodeUIModel.countryCodeUIModel = CountryCodeUIModel()
                startCreateFilterScreen()
            }
        }
    }

    private fun changeBackgroundWithFABShow(isShown: Boolean) {
        binding?.apply {
            listFilterCheck.alpha = if (isShown) 1.0f else 0.5f
            listFilterInfo.alpha = if (isShown) 1.0f else 0.5f
            listFilterEmpty.alpha = if (isShown) 1.0f else 0.5f
            listFilterRefresh.alpha = if (isShown) 1.0f else 0.5f
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(FILTER_ACTION) { _, _ ->
            val checkedFilterList =
                filterWithFilteredNumberUIModels?.filter { it.isCheckedForDelete.isTrue() }
                    .orEmpty()
            deleteFilterList(checkedFilterList)
        }
        setFragmentResultListener(FILTER_CONDITION_LIST) { _, bundle ->
            filterIndexes = bundle.getIntegerArrayList(FILTER_CONDITION_LIST)
            setFilterCheck()
            searchDataList()
        }
    }

    override fun isFiltered(): Boolean {
        return filterIndexes.isNullOrEmpty().not()
    }
}
