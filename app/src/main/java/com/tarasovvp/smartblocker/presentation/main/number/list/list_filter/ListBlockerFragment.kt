package com.tarasovvp.smartblocker.presentation.main.number.list.list_filter

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.domain.enums.FilterAction
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LIST_STATE
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.ui_models.FilterWithFilteredNumberUIModel
import com.tarasovvp.smartblocker.utils.extensions.EMPTY
import com.tarasovvp.smartblocker.utils.extensions.isTrue
import com.tarasovvp.smartblocker.utils.extensions.safeSingleObserve
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListBlockerFragment : BaseListFilterFragment<ListBlockerFilterViewModel>() {

    override val viewModelClass = ListBlockerFilterViewModel::class.java


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

    override fun onStop() {
        super.onStop()
        viewModel.savedStateHandle[LIST_STATE] = recyclerView?.layoutManager?.onSaveInstanceState()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e("saveStateTAG", "ListBlockerFragment onAttach binding?.listFilterRecyclerView?.adapter?.itemCount ${binding?.listFilterRecyclerView?.adapter?.itemCount}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.e("saveStateTAG", "ListBlockerFragment onCreateView viewModel.filterListLiveData ${viewModel.filterListLiveData.value.orEmpty().size} binding?.listFilterRecyclerView?.adapter?.itemCount ${binding?.listFilterRecyclerView?.adapter?.itemCount}")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.e("saveStateTAG", "ListBlockerFragment onViewCreated before viewModel.filterListLiveData ${viewModel.filterListLiveData.value.orEmpty().size} binding?.listFilterRecyclerView?.adapter?.itemCount ${binding?.listFilterRecyclerView?.adapter?.itemCount}")
        super.onViewCreated(view, savedInstanceState)
        Log.e("saveStateTAG", "ListBlockerFragment onViewCreated after viewModel.filterListLiveData ${viewModel.filterListLiveData.value.orEmpty().size} binding?.listFilterRecyclerView?.adapter?.itemCount ${binding?.listFilterRecyclerView?.adapter?.itemCount}")
    }

    override fun onPause() {
        super.onPause()
        Log.e("saveStateTAG", "ListBlockerFragment onPause viewModel.filterListLiveData ${viewModel.filterListLiveData.value.orEmpty().size} binding?.listFilterRecyclerView?.adapter?.itemCount ${binding?.listFilterRecyclerView?.adapter?.itemCount}")
    }

    override fun getCurrentCountryCode() {
        viewModel.getCurrentCountryCode()
    }

    override fun deleteFilterList(checkedFilterList: List<FilterWithFilteredNumberUIModel>) {
        viewModel.deleteFilterList(checkedFilterList)
    }

    override fun startDetailsFilterScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        findNavController().navigate(ListBlockerFragmentDirections.startDetailsFilterFragment(filterWithFilteredNumberUIModel = filterWithFilteredNumberUIModel))
    }

    override fun startCreateFilterScreen() {
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filter = String.EMPTY
        filterWithCountryCodeUIModel.filterWithFilteredNumberUIModel.filterType = Constants.BLOCKER
        findNavController().navigate(ListBlockerFragmentDirections.startCreateFilterFragment(filterWithCountryCodeUIModel))
    }

    override fun startFilterActionScreen(filterWithFilteredNumberUIModel: FilterWithFilteredNumberUIModel) {
        findNavController().navigate(ListBlockerFragmentDirections.startFilterActionDialog(filterWithFilteredNumberUIModel.apply { filterAction = FilterAction.FILTER_ACTION_BLOCKER_DELETE }))
    }

    override fun searchDataList() {
        (adapter as? FilterAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredFilterList(filterWithFilteredNumberUIModels.orEmpty(), searchQuery.orEmpty(), filterIndexes ?: arrayListOf())
    }

    override fun getData() {
        viewModel.getFilterList(true, swipeRefresh?.isRefreshing.isTrue())
    }
    override fun showInfoScreen() {
        findNavController().navigate(ListBlockerFragmentDirections.startInfoFragment(info = Info.INFO_LIST_BLOCKER))
    }
}
