package com.tarasovvp.smartblocker.presentation.main.number.list.list_call

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.models.entities.CallWithFilter
import com.tarasovvp.smartblocker.infrastructure.constants.Constants
import com.tarasovvp.smartblocker.infrastructure.constants.Constants.CALL_DELETE
import com.tarasovvp.smartblocker.databinding.FragmentListCallBinding
import com.tarasovvp.smartblocker.domain.enums.Info
import com.tarasovvp.smartblocker.domain.models.InfoData
import com.tarasovvp.smartblocker.presentation.MainActivity
import com.tarasovvp.smartblocker.presentation.base.BaseAdapter
import com.tarasovvp.smartblocker.presentation.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.extensions.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListCallFragment :
    BaseListFragment<FragmentListCallBinding, ListCallViewModel, CallWithFilter>() {

    override var layoutId = R.layout.fragment_list_call
    override val viewModelClass = ListCallViewModel::class.java

    private var callWithFilterList: List<CallWithFilter>? = null
    private var isDeleteMode = false

    override fun createAdapter(): BaseAdapter<CallWithFilter>? {
        return context?.let {
            CallAdapter(object : CallClickListener {
                override fun onCallClick(callWithFilter: CallWithFilter) {
                    findNavController().navigate(
                        ListCallFragmentDirections.startDetailsNumberDataFragment(
                        callWithFilter)
                    )
                }

                override fun onCallLongClick() {
                    changeDeleteMode()
                }

                override fun onCallDeleteCheckChange(callWithFilter: CallWithFilter) {
                    callWithFilterList?.find { it.call?.callDate == callWithFilter.call?.callDate }?.call?.isCheckedForDelete =
                        callWithFilter.call?.isCheckedForDelete.isTrue()
                    if (callWithFilterList?.any { it.call?.isCheckedForDelete.isTrue() }.isNotTrue() && isDeleteMode) {
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

    private fun setCallListData(callWithFilterList: List<CallWithFilter>) {
        binding?.listCallCheck?.isEnabled = callWithFilterList.isNotEmpty() || binding?.listCallCheck?.isChecked.isTrue()
        checkDataListEmptiness(callWithFilterList.isEmpty())
        viewModel.getHashMapFromCallList(callWithFilterList, swipeRefresh?.isRefreshing.isTrue())
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
            callWithFilterList?.filter { it.call?.isCheckedForDelete.isTrue() }?.map { it.call?.callId.orZero() }?.let { viewModel.deleteCallList(it) }
        }
        setFragmentResultListener(Constants.FILTER_CONDITION_LIST) { _, bundle ->
            filterIndexes = bundle.getIntegerArrayList(Constants.FILTER_CONDITION_LIST)
            setFilterCheck()
            searchDataList()
        }
    }

    private fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.not()
        binding?.listCallCheck?.isEnabled = isDeleteMode.not()
        (adapter as CallAdapter).apply {
            isDeleteMode = this@ListCallFragment.isDeleteMode
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
    }

    private fun setDeleteMenuClickListener() {
        (activity as MainActivity).toolbar?.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.delete_menu_item) {
                val deleteCallCount = callWithFilterList?.filter { it.call?.isCheckedForDelete.isTrue() }.orEmpty().size
                this@ListCallFragment.findNavController()
                    .navigate(ListCallFragmentDirections.startFilteredCallDeleteDialog(callDelete =
                    resources.getQuantityString(R.plurals.list_call_delete_amount,
                        deleteCallCount.quantityString(),
                        if (deleteCallCount > 1) deleteCallCount else if (callWithFilterList?.firstOrNull { it.call?.isCheckedForDelete.isTrue() }?.call?.number.isNullOrEmpty()) getString(
                            R.string.details_number_hidden
                        ) else callWithFilterList?.firstOrNull { it.call?.isCheckedForDelete.isTrue() }?.call?.number
                    )
                    )
                    )
            }
            true
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callListLiveData.safeSingleObserve(viewLifecycleOwner) { callListData ->
                if (callListData == callWithFilterList) {
                    checkDataListEmptiness(callListData.isEmpty())
                    return@safeSingleObserve
                }
                callWithFilterList = callListData
                searchDataList()
            }
            filteredCallListLiveData.safeSingleObserve(viewLifecycleOwner) { filteredCallList ->
                setCallListData(filteredCallList)
            }
            callHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { callHashMap ->
                callHashMap?.let { setDataList(it) }
            }
            successDeleteNumberLiveData.safeSingleObserve(viewLifecycleOwner) {
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
        (adapter as? CallAdapter)?.searchQuery = searchQuery.orEmpty()
        viewModel.getFilteredCallList(callWithFilterList.orEmpty(), searchQuery.orEmpty(), filterIndexes ?: arrayListOf())
    }

    override fun getData() {
        viewModel.getCallList(swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        findNavController().navigate(
            ListCallFragmentDirections.startInfoFragment(
                info = InfoData(
                    title = getString(Info.INFO_CALL_LIST.title()),
                    description = getString(Info.INFO_CALL_LIST.description())
                )
            )
        )
    }
}
