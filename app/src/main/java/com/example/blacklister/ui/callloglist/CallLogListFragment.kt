package com.example.blacklister.ui.callloglist

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.blacklister.databinding.FragmentCallLogListBinding
import com.example.blacklister.extensions.formattedPhoneNumber
import com.example.blacklister.extensions.hashMapFromList
import com.example.blacklister.model.CallLog
import com.example.blacklister.model.Contact
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment
import com.example.blacklister.utils.HeaderDataItem
import com.google.gson.Gson

class CallLogListFragment :
    BaseListFragment<FragmentCallLogListBinding, CallLogListViewModel, CallLog>() {

    override fun getViewBinding() = FragmentCallLogListBinding.inflate(layoutInflater)

    override val viewModelClass = CallLogListViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        swipeRefresh = binding?.callLogListRefresh
        binding?.callLogListRecyclerView?.initRecyclerView()
    }

    override fun getDataList() {
        viewModel.getCallLogList()
    }


    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData?.observe(viewLifecycleOwner, { callLogList ->
                val callLogHashMap =
                    callLogList.sortedBy { it.dateFromTime() }.reversed().hashMapFromList()
                Log.e(
                    "hashMapTAG",
                    "CallLogListFragment callLogHashMap ${Gson().toJson(callLogHashMap)}"
                )
                for (callLogEntry in callLogHashMap) {
                    dataLoaded(
                        callLogEntry.value,
                        HeaderDataItem(
                            headerType = HeaderDataItem.HEADER_TYPE,
                            header = callLogEntry.key
                        )
                    )
                }
            })
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