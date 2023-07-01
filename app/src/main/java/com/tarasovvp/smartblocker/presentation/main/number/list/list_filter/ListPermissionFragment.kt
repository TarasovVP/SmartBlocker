package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListPermissionFragment : BaseListFilterFragment<ListPermissionFilterViewModel>() {

    override val viewModelClass = ListPermissionFilterViewModel::class.java

    override fun observeLiveData() {
        with(viewModel) {
            filterListLiveData.safeSingleObserve(viewLifecycleOwner) { filterList ->
                filterWithFilteredNumberUIModels = filterList as? ArrayList<FilterWithFilteredNumberUIModel>
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

    override fun getCurrentCountryCode() {
        viewModel.getCurrentCountryCode()
    }

    override fun deleteFilterList(checkedFilterList: List<FilterWithFilteredNumberUIModel>) {
        viewModel.deleteFilterList(checkedFilterList)
    }

    override fun startDetailsFilterScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        findNavController().navigate(ListPermissionFragmentDirections.startDetailsFilterFragment(filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel))
    }

    override fun startCreateFilterScreen() {
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filter = String.EMPTY
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filterType = Constants.PERMISSION
        findNavController().navigate(ListPermissionFragmentDirections.startCreateFilterFragment(filterWithCountryCodeUIModel))
    }

    override fun startFilterActionScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        findNavController().navigate(ListPermissionFragmentDirections.startFilterActionDialog(filterWithFilteredNumberUIModel.apply { filterAction = FilterAction.FILTER_ACTION_PERMISSION_DELETE }))
    }

    override fun searchDataList() {
        (adapter as? FilterAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredFilterList(filterWithFilteredNumberUIModels.orEmpty(), searchQuery.orEmpty(), filterIndexes ?: arrayListOf())
    }

    override fun getData() {
        viewModel.getFilterList(false, swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        findNavController().navigate(ListPermissionFragmentDirections.startInfoFragment(info = Info.INFO_LIST_BLOCKER))
    }
}