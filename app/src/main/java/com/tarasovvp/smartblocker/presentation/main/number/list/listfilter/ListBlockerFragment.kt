package com.tarasovvp.smartblocker.presentation.main.number.list.listfilter

import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IS_DELETE_MODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LIST_STATE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SAVED_LIST
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.uimodels.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isNotNull
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListBlockerFragment : BaseListFilterFragment<ListBlockerFilterViewModel>() {
    override val viewModelClass = ListBlockerFilterViewModel::class.java

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filterWithFilteredNumberUIModels =
                    filterList as? ArrayList<FilterWithFilteredNumberUIModel>
                searchDataList()
            }
            filteredFilterListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredFilterList ->
                setFilterListData(filteredFilterList)
            }
            successDeleteFilterLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as? MainActivity)?.getAllData()
                changeDeleteMode()
            }
            currentCountryCodeLiveData.safeSingleObserve(viewLifecycleOwner) { countryCodeUIModel ->
                filterWithCountryCodeUIModel.countryCodeUIModel = countryCodeUIModel
                startCreateFilterScreen()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.savedStateHandle[LIST_STATE] = recyclerView?.layoutManager?.onSaveInstanceState()
        viewModel.savedStateHandle[SAVED_LIST] = viewModel.filterListLiveData.value
        viewModel.savedStateHandle[IS_DELETE_MODE] = isDeleteMode
    }

    override fun getCurrentCountryCode() {
        viewModel.getCurrentCountryCode()
    }

    override fun deleteFilterList(checkedFilterList: List<FilterWithFilteredNumberUIModel>) {
        viewModel.deleteFilterList(checkedFilterList)
    }

    override fun startDetailsFilterScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        findNavController().navigate(
            ListBlockerFragmentDirections.startDetailsFilterFragment(
                filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel,
            ),
        )
    }

    override fun startCreateFilterScreen() {
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filter = String.EMPTY
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filterType = Constants.BLOCKER
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filterAction =
            FilterAction.FILTER_ACTION_INVALID
        findNavController().navigate(
            ListBlockerFragmentDirections.startCreateFilterFragment(
                filterWithCountryCodeUIModel,
            ),
        )
    }

    override fun startFilterActionScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        findNavController().navigate(
            ListBlockerFragmentDirections.startFilterActionDialog(
                filterWithFilteredNumberUIModel.apply {
                    filterAction = FilterAction.FILTER_ACTION_BLOCKER_DELETE
                },
            ),
        )
    }

    override fun searchDataList() {
        (adapter as? FilterAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredFilterList(
            filterWithFilteredNumberUIModels.orEmpty(),
            searchQuery.orEmpty(),
            filterIndexes ?: arrayListOf(),
        )
    }

    override fun getData(allDataChange: Boolean) {
        viewModel.savedStateHandle.get<List<FilterWithFilteredNumberUIModel>>(SAVED_LIST)
            .takeIf { it.isNotNull() && allDataChange.not() }?.let {
                viewModel.filterListLiveData.postValue(it)
                viewModel.savedStateHandle[SAVED_LIST] = null
            } ?: viewModel.getFilterList(true, swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        findNavController().navigate(ListBlockerFragmentDirections.startInfoFragment(info = Info.INFO_LIST_BLOCKER))
    }
}
