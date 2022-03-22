package com.example.blacklister.ui.callloglist

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.FragmentCallLogListBinding
import com.example.blacklister.extensions.formattedPhoneNumber
import com.example.blacklister.model.CallLog
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import java.util.*

class CallLogListFragment :
    BaseListFragment<FragmentCallLogListBinding, CallLogListViewModel, CallLog>() {

    override fun getViewBinding() = FragmentCallLogListBinding.inflate(layoutInflater)

    override val viewModelClass = CallLogListViewModel::class.java

    private var callLogList: List<CallLog>? = null

    override fun initView() {
        swipeRefresh = binding?.callLogListRefresh
        recyclerView = binding?.callLogListRecyclerView
        searchableEditText = binding?.callLogListSearch
        emptyListText = binding?.callLogListEmpty
    }

    override fun getDataList() {
        Log.e("dataTAG", "CallLogListFragment getDataList")
        swipeRefresh?.isRefreshing = true
        viewModel.getCallLogList()
    }


    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData.observe(viewLifecycleOwner, { callLogList ->
                this@CallLogListFragment.callLogList = callLogList
                if (!checkDataListEmptiness(callLogList)) {
                    getHashMapFromCallLogList(callLogList)
                }
                Log.e("dataTAG", "CallLogListFragment observeLiveData setDataList before")
            })
            callLogHashMapLiveData.observe(viewLifecycleOwner, { callLogHashMap ->
                callLogHashMap?.let { setDataList(it) }
                Log.e("dataTAG", "CallLogListFragment observeLiveData callLogHashMapLiveData callLogHashMap.size ${callLogHashMap?.size}")
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
                            phone = callLog.phone?.formattedPhoneNumber(),
                            isBlackList = callLog.isBlackList
                        )
                    )
                )
            }
        }
    }
}