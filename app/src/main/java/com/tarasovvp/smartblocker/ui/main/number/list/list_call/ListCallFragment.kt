package com.tarasovvp.smartblocker.ui.main.number.list.list_call

import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
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
import com.tarasovvp.smartblocker.models.Call
import com.tarasovvp.smartblocker.models.InfoData
import com.tarasovvp.smartblocker.models.LogCall
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.base.BaseAdapter
import com.tarasovvp.smartblocker.ui.base.BaseListFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
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
                    findNavController().navigate(ListCallFragmentDirections.startDetailsNumberDataFragment(
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
            val callListToDelete = arrayListOf<Call>().apply {
                callList?.forEach { call ->
                    if (call.isCheckedForDelete && call.isBlockedCall()) add(call)
                    if (call.isCheckedForDelete && call is LogCall) add(call)
                }
            }
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
                val deleteCallCount = callList?.filter { it.isCheckedForDelete }.orEmpty().size
                this@ListCallFragment.findNavController()
                    .navigate(ListCallFragmentDirections.startFilteredCallDeleteDialog(callDelete =
                    resources.getQuantityString(R.plurals.list_call_delete_amount,
                        deleteCallCount.quantityString(),
                        if (deleteCallCount > 1) deleteCallCount else if (callList?.firstOrNull { it.isCheckedForDelete }?.number.isNullOrEmpty()) getString(
                            R.string.details_number_hidden) else callList?.firstOrNull { it.isCheckedForDelete }?.number)))
            }
            true
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
