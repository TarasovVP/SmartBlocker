package com.tarasovvp.blacklister.ui.main.callloglist

import android.content.IntentFilter
import android.util.Log
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.ADD_BLACK_NUMBER
import com.tarasovvp.blacklister.constants.Constants.BLACK_NUMBER
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.CALL_RECEIVE
import com.tarasovvp.blacklister.databinding.FragmentCallLogListBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.CallHandleReceiver
import java.util.*

class CallLogListFragment :
    BaseListFragment<FragmentCallLogListBinding, CallLogListViewModel, CallLog>() {

    override fun getViewBinding() = FragmentCallLogListBinding.inflate(layoutInflater)

    override val viewModelClass = CallLogListViewModel::class.java

    private var callLogList: List<CallLog>? = null
    private var callHandleReceiver: CallHandleReceiver? = null

    override fun createAdapter(): BaseAdapter<CallLog>? {
        return context?.let {
            CallLogAdapter { callLog, view ->
                val listener = PopupMenu.OnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.change -> {
                            if (callLog.isBlackList) {
                                findNavController().navigate(CallLogListFragmentDirections.startInfoDialog(
                                    blackNumber = BlackNumber(blackNumber = callLog.phone.toString())))
                            } else {
                                findNavController().navigate(CallLogListFragmentDirections.startBlackNumberAddFragment(
                                    BlackNumber(blackNumber = callLog.phone.toString())))
                            }
                        }
                    }
                    true
                }
                it.showPopUpMenu(R.menu.contact_menu, view, listener)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).apply {
            startService()
        }
        Log.e("callReceiveTAG", "CallLogListFragment onStart")
        callHandleReceiver = CallHandleReceiver {
            Log.e("callReceiveTAG", "CallLogListFragment CallHandleReceiver string $it")
            getAllData()
        }
        context?.registerReceiver(callHandleReceiver, IntentFilter(CALL_RECEIVE))
    }

    override fun onStop() {
        super.onStop()
        Log.e("callReceiveTAG", "CallLogListFragment onStop")
        context?.unregisterReceiver(callHandleReceiver)
    }

    override fun onResume() {
        super.onResume()
        binding?.callLogListCheck?.isChecked = false
    }

    override fun initView() {
        swipeRefresh = binding?.callLogListRefresh
        recyclerView = binding?.callLogListRecyclerView
        searchableEditText = binding?.callLogListSearch
        emptyListText = binding?.callLogListEmpty
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BlackNumber>(
            BLACK_NUMBER)?.observe(viewLifecycleOwner) { blackNumber ->
            blackNumber?.let {
                viewModel.deleteBlackNumber(it)
            }
        }
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>(
            ADD_BLACK_NUMBER)?.observe(viewLifecycleOwner) {
            Log.e("callReceiveTAG",
                "CallLogListFragment getLiveData ADD_BLACK_NUMBER $it")
            getAllData()
        }
        binding?.callLogListCheck?.setOnCheckedChangeListener { _, _ ->
            searchDataList()
        }
    }

    private fun getAllData() {
        (activity as MainActivity).apply {
            getAllData()
            mainViewModel.successLiveData.safeSingleObserve(this, { success ->
                Log.e("callReceiveTAG",
                    "CallLogListFragment successLiveData.safeSingleObserve success $success")
                viewModel.getCallLogList()
            })
        }
    }

    override fun getDataList() {
        swipeRefresh?.isRefreshing = true
        viewModel.getCallLogList()
    }

    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData.safeObserve(viewLifecycleOwner, { callLogList ->
                this@CallLogListFragment.callLogList = callLogList
                Log.e("callReceiveTAG",
                    "CallLogListFragment callLogLiveData.safeObserve callLogList.size ${callLogList.size} checkDataListEmptiness(callLogList) ${
                        checkDataListEmptiness(callLogList)
                    }")
                if (!checkDataListEmptiness(callLogList)) {
                    getHashMapFromCallLogList(callLogList)
                }
            })
            callLogHashMapLiveData.safeSingleObserve(viewLifecycleOwner, { callLogHashMap ->
                callLogHashMap?.let { setDataList(it) }
            })
            deleteSuccessLiveData.safeSingleObserve(viewLifecycleOwner, {
                getAllData()
            })
        }
    }

    override fun searchDataList() {
        val filteredCallLogList = callLogList?.filter { callLog ->
            (callLog.name?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ).isTrue() || callLog.phone?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            )
                .isTrue()) && if (binding?.callLogListCheck?.isChecked.isTrue()) callLog.type == BLOCKED_CALL else true
        } as? ArrayList<CallLog>
        filteredCallLogList?.apply {
            if (!checkDataListEmptiness(this)) {
                viewModel.getHashMapFromCallLogList(this)
            }
        }
    }
}