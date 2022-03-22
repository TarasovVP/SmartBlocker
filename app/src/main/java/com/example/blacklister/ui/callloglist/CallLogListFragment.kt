package com.example.blacklister.ui.callloglist

import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.FragmentCallLogListBinding
import com.example.blacklister.extensions.formattedPhoneNumber
import com.example.blacklister.extensions.hashMapFromList
import com.example.blacklister.model.CallLog
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.example.blacklister.utils.HeaderDataItem
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
    }

    override fun getDataList() {
        viewModel.getCallLogList()
    }


    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData?.observe(viewLifecycleOwner, { callLogList ->
                this@CallLogListFragment.callLogList = callLogList
                setCallLogList(callLogList)
            })
        }
    }

    private fun setCallLogList(callLogList: List<CallLog>) {
        val callLogHashMap =
            callLogList.sortedBy { it.dateFromTime() }.reversed().hashMapFromList()
        adapter?.clearData()
        if (callLogHashMap.isEmpty()) adapter?.clearData()
        for (callLogEntry in callLogHashMap) {
            dataLoaded(
                callLogEntry.value,
                HeaderDataItem(
                    headerType = HeaderDataItem.HEADER_TYPE,
                    header = callLogEntry.key
                )
            )
        }
        adapter?.notifyDataSetChanged()
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
        setCallLogList(filteredCallLogList)
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