package com.tarasovvp.smartblocker.ui.number_data.list.list_call

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
import com.tarasovvp.smartblocker.databinding.FragmentListCallBinding
import com.tarasovvp.smartblocker.enums.FilterAction
import com.tarasovvp.smartblocker.enums.Info
import com.tarasovvp.smartblocker.extensions.*
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.InfoData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.ui.base.BaseListFragment
import com.tarasovvp.smartblocker.utils.setSafeOnClickListener
import java.util.*

class ListCallFragment :
    BaseListFragment<FragmentListCallBinding, ListCallViewModel, Call>() {

    override var layoutId = R.layout.fragment_list_call
    override val viewModelClass = ListCallViewModel::class.java

    private val args: ListCallFragmentArgs by navArgs()

    private var callList: List<Call>? = null
    private var isDeleteMode = false
    private var conditionFilterIndexes: ArrayList<Int>? = null

    override fun createAdapter(): BaseAdapter<Call>? {
        return context?.let {
            CallAdapter(object : CallClickListener {
                override fun onCallClick(call: Call) {
                    findNavController().navigate(ListCallFragmentDirections.startNumberDataDetailFragment(
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

    override fun initViews() {
        binding?.apply {
            swipeRefresh = listCallRefresh
            recyclerView = listCallRecyclerView
            emptyStateContainer = listCallEmpty
            listCallRecyclerView.hideKeyboardWithLayoutTouch()
        }
        args.searchQuery?.let {
            searchQuery = it
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
        isDeleteMode = isDeleteMode.not()
        (adapter as CallAdapter).apply {
            isDeleteMode = this@ListCallFragment.isDeleteMode
            recyclerView?.post {
                adapter?.notifyDataSetChanged()
            }
        }
        (activity as MainActivity).toolbar?.apply {
            title =
                if (isDeleteMode) getString(R.string.delete_) else getString(if (binding?.listCallCheck?.isChecked.isTrue()) R.string.log_list else R.string.blocked_call_log)
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
        (activity as MainActivity).toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_menu_item -> {
                    this@ListCallFragment.findNavController()
                        .navigate(ListCallFragmentDirections.startFilterActionDialog(filterAction = FilterAction.FILTER_ACTION_BLOCKER_DELETE))
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