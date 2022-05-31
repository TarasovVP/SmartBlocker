package com.tarasovvp.blacklister.ui.main.callloglist

import android.content.IntentFilter
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.constants.Constants.CALL_RECEIVE
import com.tarasovvp.blacklister.databinding.FragmentCallLogListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeObserve
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.toFormattedPhoneNumber
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.model.Contact
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

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).apply {
            startService()
        }
        Log.e("callReceiveTAG", "CallLogListFragment onStart")
        callHandleReceiver = CallHandleReceiver {
            Log.e("callReceiveTAG", "CallLogListFragment CallHandleReceiver string $it")
            (activity as MainActivity).apply {
                getAllData()
                mainViewModel.successLiveData.safeSingleObserve(this, { success ->
                    Log.e("callReceiveTAG", "CallLogListFragment successLiveData.safeSingleObserve success $success")
                    viewModel.getCallLogList()
                })
            }
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
            ).isTrue() || callLog.phone?.lowercase(Locale.getDefault())?.contains(
                searchableEditText?.text.toString()
                    .lowercase(Locale.getDefault())
            ).isTrue()
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