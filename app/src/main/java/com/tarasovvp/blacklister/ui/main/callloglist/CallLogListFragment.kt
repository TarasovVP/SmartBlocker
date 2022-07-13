package com.tarasovvp.blacklister.ui.main.callloglist

import android.content.IntentFilter
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.CALL_RECEIVE
import com.tarasovvp.blacklister.databinding.FragmentCallLogListBinding
import com.tarasovvp.blacklister.extensions.*
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
            CallLogAdapter { number ->
                findNavController().navigate(CallLogListFragmentDirections.startNumberDetailFragment(
                    number = number))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).apply {
            startService()
        }
        callHandleReceiver = CallHandleReceiver {
            getAllData()
        }
        context?.registerReceiver(callHandleReceiver, IntentFilter(CALL_RECEIVE))
    }

    override fun onStop() {
        super.onStop()
        context?.unregisterReceiver(callHandleReceiver)
    }

    override fun onResume() {
        super.onResume()
        binding?.callLogListCheck?.isChecked = false
    }

    override fun initView() {
        swipeRefresh = binding?.callLogListRefresh
        recyclerView = binding?.callLogListRecyclerView
        emptyListText = binding?.callLogListEmpty
        binding?.callLogListCheck?.setOnCheckedChangeListener { _, _ ->
            searchDataList()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData.safeObserve(viewLifecycleOwner) { callLogList ->
                this@CallLogListFragment.callLogList = callLogList
                if (!checkDataListEmptiness(callLogList)) {
                    getHashMapFromCallLogList(callLogList)
                }
            }
            callLogHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { callLogHashMap ->
                callLogHashMap?.let { setDataList(it) }
            }
        }
        (activity as MainActivity).apply {
            mainViewModel.successAllDataLiveData.safeSingleObserve(this) {
                viewModel.getCallLogList()
            }
        }
    }

    override fun searchDataList() {
        val filteredCallLogList = callLogList?.filter { callLog ->
            (callLog.name?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue() || callLog.phone?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
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