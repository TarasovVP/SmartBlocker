package com.example.blacklister.ui.callloglist

import android.os.Bundle
import android.view.View
import com.example.blacklister.databinding.FragmentCallLogListBinding
import com.example.blacklister.model.CallLog
import com.example.blacklister.ui.base.BaseAdapter
import com.example.blacklister.ui.base.BaseListFragment

class CallLogListFragment :
    BaseListFragment<FragmentCallLogListBinding, CallLogListViewModel, CallLog>() {

    override fun getViewBinding() = FragmentCallLogListBinding.inflate(layoutInflater)

    override val viewModelClass = CallLogListViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.callLogListRecyclerView?.initRecyclerView()
        viewModel.getCallLogList()
    }

    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData?.observe(viewLifecycleOwner, { callLogList ->
                onInitialDataLoaded(callLogList)
            })
        }
    }

    override fun createAdapter(): BaseAdapter<CallLog, *>? {
        return context?.let {
            CallLogAdapter(object : CallLogClickListener {
                override fun onCallLogClicked(callLog: CallLog) {

                }
            })
        }
    }

}