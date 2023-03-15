package com.tarasovvp.smartblocker.ui.main.number.list.list_call

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.CALL_DELETE
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.constants.Constants.PERMITTED_CALL
import com.tarasovvp.smartblocker.databinding.FragmentListCallBinding
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.database.entities.CallWithFilter
import com.tarasovvp.smartblocker.models.InfoData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.ui.base.BaseListFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ListCallFragment :
    BaseListFragment<FragmentListCallBinding, ListCallViewModel, CallWithFilter>() {

    override var layoutId = R.layout.fragment_list_call
    override val viewModelClass = ListCallViewModel::class.java

    private var callWithFilterList: List<CallWithFilter>? = null
    private var isDeleteMode = false
    private var conditionFilterIndexes: ArrayList<Int>? = null

    override fun createAdapter(): BaseAdapter<CallWithFilter>? {
        return context?.let {
            CallAdapter(object : CallClickListener {
                override fun onCallClick(callWithFilter: CallWithFilter) {
                    findNavController().navigate(ListCallFragmentDirections.startDetailsNumberDataFragment(
                        callWithFilter))
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
            emptyStateContainer = listCallEmpty
            listCallRecyclerView.hideKeyboardWithLayoutTouch()
        }
        setCallConditionFilter()
    }

    private fun setCallConditionFilter() {
        conditionFilterIndexes = conditionFilterIndexes ?: arrayListOf()
        binding?.listCallCheck?.apply {
            isSelected = true
            val callFilteringText = arrayListOf<String>()
            if (conditionFilterIndexes.isNullOrEmpty())
                callFilteringText.add(getString(R.string.filter_no_filter))
            else {
                if (conditionFilterIndexes?.contains(BLOCKED_CALL.toInt()).isTrue())
                    callFilteringText.add(context.getString(R.string.filter_call_blocked))
                if (conditionFilterIndexes?.contains(PERMITTED_CALL.toInt()).isTrue())
                    callFilteringText.add(context.getString(R.string.filter_call_permitted))
            }
            text = callFilteringText.joinToString()
            isChecked = conditionFilterIndexes.isNullOrEmpty().not()
            isEnabled =
                adapter?.itemCount.orZero() > 0 || conditionFilterIndexes.isNullOrEmpty().not()
        }
    }

    override fun setClickListeners() {
        binding?.listCallCheck?.setSafeOnClickListener {
            binding?.root?.hideKeyboard()
            binding?.listCallCheck?.isChecked = binding?.listCallCheck?.isChecked.isTrue().not()
            findNavController().navigate(
                ListCallFragmentDirections.startNumberDataFilteringDialog(isCallList = true,
                    filteringList = conditionFilterIndexes.orEmpty().toIntArray()))
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
            conditionFilterIndexes = bundle.getIntegerArrayList(Constants.FILTER_CONDITION_LIST)
            setCallConditionFilter()
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
                            R.string.details_number_hidden) else callWithFilterList?.firstOrNull { it.call?.isCheckedForDelete.isTrue() }?.call?.number)))
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
        return conditionFilterIndexes.isNullOrEmpty().not()
    }

    override fun searchDataList() {
        (adapter as? CallAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredCallList = callWithFilterList?.filter { callWithFilter ->
            (callWithFilter.call?.callName?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue()
                    || callWithFilter.call?.number?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue())
                    && (callWithFilter.filterWithCountryCode?.filter?.isBlocker().isTrue() && conditionFilterIndexes?.contains(
                BLOCKER).isTrue() ||
                    callWithFilter.filterWithCountryCode?.filter?.isPermission().isTrue() && conditionFilterIndexes?.contains(
                PERMISSION).isTrue() ||
                    callWithFilter.call?.isBlockedCall().isTrue() && conditionFilterIndexes?.contains(
                BLOCKED_CALL.toInt()).isTrue() ||
                    callWithFilter.call?.isPermittedCall().isTrue() && conditionFilterIndexes?.contains(
                PERMITTED_CALL.toInt()).isTrue()
                    || conditionFilterIndexes.isNullOrEmpty())
        }.orEmpty()
        binding?.listCallCheck?.isEnabled =
            filteredCallList.isNotEmpty() || binding?.listCallCheck?.isChecked.isTrue()
        checkDataListEmptiness(filteredCallList.isEmpty())
        if (filteredCallList.isNotEmpty()) {
            viewModel.getHashMapFromCallList(filteredCallList, swipeRefresh?.isRefreshing.isTrue())
        }
    }

    override fun getData() {
        viewModel.getCallList(swipeRefresh?.isRefreshing.isTrue())
    }

    override fun showInfoScreen() {
        findNavController().navigate(ListCallFragmentDirections.startInfoFragment(info = InfoData(
            title = getString(Info.INFO_CALL_LIST.title),
            description = getString(Info.INFO_CALL_LIST.description))))
    }
}
