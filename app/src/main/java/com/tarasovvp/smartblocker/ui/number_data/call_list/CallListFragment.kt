package com.tarasovvp.smartblocker.ui.number_data.call_list

import android.util.Log
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.constants.Constants
import com.tarasovvp.smartblocker.constants.Constants.BLOCKED_CALL
import com.tarasovvp.smartblocker.constants.Constants.BLOCKER
import com.tarasovvp.smartblocker.constants.Constants.FILTER_ACTION
import com.tarasovvp.smartblocker.constants.Constants.PERMISSION
import com.tarasovvp.smartblocker.constants.Constants.PERMITTED_CALL
import com.tarasovvp.smartblocker.databinding.FragmentCallListBinding
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.ui.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*

class CallListFragment :
    BaseListFragment<FragmentCallListBinding, CallListViewModel, Call>() {

    override var layoutId = R.layout.fragment_call_list
    override val viewModelClass = CallListViewModel::class.java

    private val args: CallListFragmentArgs by navArgs()

    private var callList: List<Call>? = null
    private var isDeleteMode = false
    private var conditionFilterIndexes: ArrayList<Int>? = null

    override fun createAdapter(): BaseAdapter<Call>? {
        Log.e("adapterTAG", "CallListFragment createAdapter callList?.size ${callList?.size}")
        return context?.let {
            CallAdapter(object : CallClickListener {
                override fun onCallClick(call: Call) {
                    findNavController().navigate(CallListFragmentDirections.startNumberDataDetailFragment(
                        call))
                }

                override fun onCallLongClick() {
                    changeDeleteMode()
                }

                override fun onCallDeleteCheckChange(call: Call) {
                    callList?.find { it.callDate == call.callDate }?.isCheckedForDelete =
                        call.isCheckedForDelete
                    if (callList?.any { it.isCheckedForDelete }.isNotTrue() && isDeleteMode) {
                        changeDeleteMode()
                    }
                }

                override fun onCallDeleteInfoClick() {
                    showMessage(getString(R.string.call_delete_info), true)
                }
            })
        }
    }

    override fun initView() {
        binding?.apply {
            swipeRefresh = callListRefresh
            recyclerView = callListRecyclerView
            emptyStateContainer = callListEmpty
            callListRecyclerView.hideKeyboardWithLayoutTouch()
        }
        Log.e("searchTAG", "CallListFragment initView args.searchQuery ${args.searchQuery}")
        args.searchQuery?.let {
            searchQuery = it
        }
        setCallConditionFilter()
        setClickListeners()
        setFragmentResultListeners()
    }

    private fun setCallConditionFilter() {
        conditionFilterIndexes = conditionFilterIndexes ?: arrayListOf()
        binding?.callListCheck?.apply {
            isSelected = true
            val callFilteringText = arrayListOf<String>()
            if (conditionFilterIndexes.isNullOrEmpty())
                callFilteringText.add(getString(R.string.filter_no_filter))
            else {
                if (conditionFilterIndexes?.contains(BLOCKER).isTrue())
                    callFilteringText.add(context.getString(R.string.with_blocker_filter))
                if (conditionFilterIndexes?.contains(PERMISSION).isTrue())
                    callFilteringText.add(context.getString(R.string.with_permission_filter))
                if (conditionFilterIndexes?.contains(BLOCKED_CALL.toInt()).isTrue())
                    callFilteringText.add(context.getString(R.string.by_blocker_filter))
                if (conditionFilterIndexes?.contains(PERMITTED_CALL.toInt()).isTrue())
                    callFilteringText.add(context.getString(R.string.by_permission_filter))
            }
            text = callFilteringText.joinToString()
            isChecked = conditionFilterIndexes.isNullOrEmpty().not()
            isEnabled =
                adapter?.itemCount.orZero() > 0 || conditionFilterIndexes.isNullOrEmpty().not()
        }
    }

    private fun setClickListeners() {
        binding?.callListCheck?.setSafeOnClickListener {
            binding?.root?.hideKeyboard()
            binding?.callListCheck?.isChecked = binding?.callListCheck?.isChecked.isTrue().not()
            findNavController().navigate(
                CallListFragmentDirections.startNumberDataFilteringDialog(isCallList = true,
                    filteringList = conditionFilterIndexes.orEmpty().toIntArray()))
        }
        binding?.callListInfo?.setSafeOnClickListener {
            binding?.callListInfo?.showPopUpWindow(Info.INFO_CALL_LIST)
        }
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(FILTER_ACTION) { _, _ ->
            viewModel.deleteCallList(callList?.filter { it.isCheckedForDelete }.orEmpty())
        }
        setFragmentResultListener(Constants.FILTER_CONDITION_LIST) { _, bundle ->
            conditionFilterIndexes = bundle.getIntegerArrayList(Constants.FILTER_CONDITION_LIST)
            setCallConditionFilter()
            searchDataList()
        }
    }

    private fun changeDeleteMode() {
        Log.e("destinationTAG", "CallListFragment changeDeleteMode isDeleteMode $isDeleteMode")
        isDeleteMode = isDeleteMode.not()
        (adapter as CallAdapter).apply {
            isDeleteMode = this@CallListFragment.isDeleteMode
            recyclerView?.post {
                adapter?.notifyDataSetChanged()
            }
        }
        (activity as MainActivity).toolbar?.apply {
            Log.e("callTAG", "CallListFragment menu $menu")
            title =
                if (isDeleteMode) getString(R.string.delete_) else getString(if (binding?.callListCheck?.isChecked.isTrue()) R.string.log_list else R.string.blocked_call_log)
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
        Log.e("callTAG", "CallListFragment setToolBarMenuClickListener")
        (activity as MainActivity).toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_menu_item -> {
                    this@CallListFragment.findNavController()
                        .navigate(CallListFragmentDirections.startFilterActionDialog(filterAction = FilterAction.FILTER_ACTION_BLOCKER_DELETE))
                    true
                }
                R.id.close_menu_item -> {
                    (adapter as CallAdapter).apply {
                        isDeleteMode = false
                        callList?.forEach {
                            it.isCheckedForDelete = false
                        }
                        changeDeleteMode()
                        notifyDataSetChanged()
                    }
                    true
                }
                else -> return@setOnMenuItemClickListener true
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callListLiveData.safeSingleObserve(viewLifecycleOwner) { callListData ->
                if (callListData == callList) {
                    checkDataListEmptiness(callListData.isEmpty())
                    return@safeSingleObserve
                }
                callList = callListData
                searchDataList()
            }
            callHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { callHashMap ->
                callHashMap?.let { setDataList(it) }
            }
            successDeleteNumberLiveData.safeSingleObserve(viewLifecycleOwner) {
                (callList as ArrayList<Call>).removeAll { it.isCheckedForDelete }
                changeDeleteMode()
                searchDataList()
            }
        }
    }

    override fun isFiltered(): Boolean {
        return conditionFilterIndexes.isNullOrEmpty().not()
    }

    override fun searchDataList() {
        Log.e("callTAG", "CallListFragment searchDataList() start")
        (adapter as? CallAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredCallList = callList?.filter { call ->
            (call.callName?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue()
                    || call.number.lowercase(Locale.getDefault())
                .contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue())
                    && (call.filter?.isBlocker().isTrue() && conditionFilterIndexes?.contains(
                BLOCKER).isTrue() ||
                    call.filter?.isPermission().isTrue() && conditionFilterIndexes?.contains(
                PERMISSION).isTrue() ||
                    call.isBlockedCall().isTrue() && conditionFilterIndexes?.contains(
                BLOCKED_CALL.toInt()).isTrue() ||
                    call.isPermittedCall().isTrue() && conditionFilterIndexes?.contains(
                PERMITTED_CALL.toInt()).isTrue()
                    || conditionFilterIndexes.isNullOrEmpty())
        }.orEmpty()
        Log.e("callTAG",
            "CallListFragment searchDataList() filteredCallList size ${filteredCallList.size}")
        binding?.callListCheck?.isEnabled =
            filteredCallList.isNotEmpty() || binding?.callListCheck?.isChecked.isTrue()
        checkDataListEmptiness(filteredCallList.isEmpty())
        if (filteredCallList.isNotEmpty()) {
            viewModel.getHashMapFromCallList(filteredCallList, swipeRefresh?.isRefreshing.isTrue())
        }
    }

    override fun getData() {
        viewModel.getCallList(swipeRefresh?.isRefreshing.isTrue())
    }
}
