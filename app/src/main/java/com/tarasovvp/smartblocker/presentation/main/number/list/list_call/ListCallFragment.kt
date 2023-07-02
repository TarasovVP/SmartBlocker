package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.FragmentListCallBinding
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CALL_DELETE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.IS_DELETE_MODE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.LIST_STATE
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.SAVED_LIST
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.ui_models.CallWithFilterUIModel
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListCallFragment :
    BaseListFragment<FragmentListCallBinding, ListCallViewModel, CallWithFilterUIModel>() {

    override var layoutId = R.layout.fragment_list_call
    override val viewModelClass = ListCallViewModel::class.java

    private var callWithFilterList: List<CallWithFilterUIModel>? = null
    private var isDeleteMode: Boolean? = null

    override fun createAdapter(): BaseAdapter<CallWithFilterUIModel>? {
        return context?.let {
             CallAdapter(object : CallClickListener {
                override fun onCallClick(callWithFilter: CallWithFilterUIModel) {
                    findNavController().navigate(ListCallFragmentDirections.startDetailsNumberDataFragment(callWithFilter))
                }

                override fun onCallLongClick() {
                    changeDeleteMode()
                }

                override fun onCallDeleteCheckChange(callWithFilter: CallWithFilterUIModel) {
                    callWithFilterList?.find { it.callDate == callWithFilter.callDate }?.isCheckedForDelete =
                        callWithFilter.isCheckedForDelete
                    if (callWithFilterList?.any { it.isCheckedForDelete }.isNotTrue() && isDeleteMode.isTrue()) {
                        changeDeleteMode()
                    }
                }

                override fun onCallDeleteInfoClick() {
                    showMessage(getString(R.string.list_call_delete_info), true)
                }
            })
        }
    }

    override fun initViews() {
        binding?.apply {
            swipeRefresh = listCallRefresh
            recyclerView = listCallRecyclerView
            filterCheck = listCallCheck
            emptyStateContainer = listCallEmpty
            listCallRecyclerView.hideKeyboardWithLayoutTouch()
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.savedStateHandle[LIST_STATE] = recyclerView?.layoutManager?.onSaveInstanceState()
        viewModel.savedStateHandle[SAVED_LIST] = viewModel.callListLiveData.value
        viewModel.savedStateHandle[IS_DELETE_MODE] = isDeleteMode
    }

    private fun setCallListData(callWithFilterList: List<CallWithFilterUIModel>) {
        binding?.listCallCheck?.isEnabled = callWithFilterList.isNotEmpty() || binding?.listCallCheck?.isChecked.isTrue()
        checkDataListEmptiness(callWithFilterList.isEmpty())
        setDataList(callWithFilterList.sortedByDescending {
            it.callDate
        }.groupBy { it.dateFromCallDate() })
        viewModel.savedStateHandle.restoreListInstantState(LIST_STATE, recyclerView?.layoutManager)
        viewModel.savedStateHandle.get<Boolean>(IS_DELETE_MODE)?.apply {
            isDeleteMode = this.not()
            viewModel.savedStateHandle[IS_DELETE_MODE] = null
            changeDeleteMode()
        }
    }

    override fun setClickListeners() {
        binding?.listCallCheck?.setSafeOnClickListener {
            binding?.root?.hideKeyboard()
            binding?.listCallCheck?.isChecked = binding?.listCallCheck?.isChecked.isTrue().not()
            findNavController().navigate(
                ListCallFragmentDirections.startNumberDataFilteringDialog(
                    previousDestinationId = findNavController().currentDestination?.id.orZero(),
                    filteringList = filterIndexes.orEmpty().toIntArray()
                )
            )
        }
        binding?.listCallInfo?.setSafeOnClickListener {
            showInfoScreen()
        }
    }

    override fun setFragmentResultListeners() {
        setFragmentResultListener(CALL_DELETE) { _, _ ->
            callWithFilterList?.filter { it.isCheckedForDelete }?.map { it.callId }?.let { viewModel.deleteCallList(it) }
        }
        setFragmentResultListener(Constants.FILTER_CONDITION_LIST) { _, bundle ->
            filterIndexes = bundle.getIntegerArrayList(Constants.FILTER_CONDITION_LIST)
            setFilterCheck()
            searchDataList()
        }
    }

    private fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.isNotTrue()
        binding?.listCallCheck?.isEnabled = isDeleteMode.isNotTrue()
        (adapter as CallAdapter).apply {
            isDeleteMode = this@ListCallFragment.isDeleteMode.isTrue()
            recyclerView?.post {
                adapter?.notifyDataSetChanged()
            }
        }
        (activity as? MainActivity)?.toolbar?.apply {
            menu?.clear()
            if (isDeleteMode.isTrue()) {
                inflateMenu(R.menu.toolbar_delete)
                setDeleteMenuClickListener()
            } else {
                inflateMenu(R.menu.toolbar_search)
                setSearchViewMenu()
            }
        }
    }

    private fun setDeleteMenuClickListener() {
        (activity as? MainActivity)?.toolbar?.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.delete_menu_item) {
                val deleteCallCount = callWithFilterList?.filter { it.isCheckedForDelete.isTrue() }.orEmpty().size
                this@ListCallFragment.findNavController()
                    .navigate(ListCallFragmentDirections.startFilteredCallDeleteDialog(callDelete =
                    resources.getQuantityString(R.plurals.list_call_delete_amount,
                        deleteCallCount.quantityString(),
                        if (deleteCallCount > 1) deleteCallCount else if (callWithFilterList?.firstOrNull { it.isCheckedForDelete }?.number.isNullOrEmpty()) getString(
                            R.string.details_number_hidden
                        ) else callWithFilterList?.firstOrNull { it.isCheckedForDelete }?.number)))
            }
            true
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callListLiveData.safeSingleObserve(viewLifecycleOwner) { callListData ->
                callWithFilterList = callListData
                searchDataList()
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                setCallListData(filteredCallList)
            }
            successDeleteNumberLiveData.safeSingleObserve(viewLifecycleOwner) {
                (activity as? MainActivity)?.apply {
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
        (adapter as? CallAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredCallList(callWithFilterList.orEmpty(), searchQuery.orEmpty(), filterIndexes ?: arrayListOf())
    }

    override fun getData() {
        viewModel.savedStateHandle.get<List<CallWithFilterUIModel>>(SAVED_LIST).takeIf { it.isNotNull() }?.let {
            viewModel.callListLiveData.postValue(it)
            viewModel.savedStateHandle[SAVED_LIST] = null
        } ?: viewModel.getCallList(swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        findNavController().navigate(
            ListCallFragmentDirections.startInfoFragment(info = Info.INFO_LIST_CALL))
    }
}
