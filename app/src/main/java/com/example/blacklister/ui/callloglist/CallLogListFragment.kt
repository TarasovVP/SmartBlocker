package com.example.blacklister.ui.callloglist

import android.content.IntentFilter
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.blacklister.constants.Constants.CALL_RECEIVE
import com.example.blacklister.databinding.FragmentCallLogListBinding
import com.example.blacklister.extensions.safeObserve
import com.example.blacklister.extensions.safeSingleObserve
import com.example.blacklister.extensions.toFormattedPhoneNumber
import com.example.blacklister.model.CallLog
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.example.blacklister.utils.CallHandleReceiver
import java.util.*

class CallLogListFragment :
    BaseListFragment<FragmentCallLogListBinding, CallLogListViewModel, CallLog>() {

    override fun getViewBinding() = FragmentCallLogListBinding.inflate(layoutInflater)

    override val viewModelClass = CallLogListViewModel::class.java

    private var callLogList: List<CallLog>? = null
    private var callHandleReceiver: CallHandleReceiver? = null

    override fun onStart() {
        super.onStart()
        Log.e("callReceiveTAG", "CallLogListFragment onStart")
        callHandleReceiver = CallHandleReceiver {
            Log.e("callReceiveTAG", "CallLogListFragment CallHandleReceiver string $it")
            viewModel.getCallLogList()
        }
        context?.registerReceiver(callHandleReceiver, IntentFilter(CALL_RECEIVE))
    }

    override fun onStop() {
        super.onStop()
        Log.e("callReceiveTAG", "CallLogListFragment onStop")
        context?.unregisterReceiver(callHandleReceiver)
    }

    override fun initView() {
        swipeRefresh = binding?.callLogListRefresh
        recyclerView = binding?.callLogListRecyclerView
        searchableEditText = binding?.callLogListSearch
        emptyListText = binding?.callLogListEmpty
    }

    override fun getDataList() {
        swipeRefresh?.isRefreshing = true
        viewModel.getCallLogList()
    }


    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData.safeObserve(viewLifecycleOwner, { callLogList ->
                this@CallLogListFragment.callLogList = callLogList
                if (!checkDataListEmptiness(callLogList)) {
                    getHashMapFromCallLogList(callLogList)
                }
            })
            callLogHashMapLiveData.safeSingleObserve(viewLifecycleOwner, { callLogHashMap ->
                callLogHashMap?.let { setDataList(it) }
            })
        }
    }

    override fun filterDataList() {
        val filteredCallLogList = callLogList?.filter { callLog ->
            callLog.name?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ) == true || callLog.phone?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ) == true
        } as ArrayList<CallLog>
        if (!checkDataListEmptiness(filteredCallLogList)) {
            viewModel.getHashMapFromCallLogList(filteredCallLogList)
        }
    }

    override fun createAdapter(): BaseAdapter<CallLog>? {
        return context?.let {
            CallLogAdapter { callLog ->
                findNavController().navigate(
                    CallLogListFragmentDirections.startContactDetail(
                        contact = Contact(
                            name = callLog.name,
                            phone = callLog.phone?.toFormattedPhoneNumber(),
                            isBlackList = callLog.isBlackList
                        )
                    )
                )
            }
        }
    }
}