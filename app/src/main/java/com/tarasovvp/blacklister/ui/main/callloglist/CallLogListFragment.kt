package com.tarasovvp.blacklister.ui.main.callloglist

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.databinding.FragmentCallLogListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.showMessage
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import java.util.*

class CallLogListFragment :
    BaseListFragment<FragmentCallLogListBinding, CallLogListViewModel, CallLog>() {

    override fun getViewBinding() = FragmentCallLogListBinding.inflate(layoutInflater)

    override val viewModelClass = CallLogListViewModel::class.java

    private var callLogList: List<CallLog>? = null

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

    }

    override fun onResume() {
        super.onResume()
        binding?.callLogListCheck?.isChecked = false
    }

    override fun initView() {
        swipeRefresh = binding?.callLogListRefresh
        recyclerView = binding?.callLogListRecyclerView
        emptyListText = binding?.callLogListEmpty
        priorityText = binding?.callLogListPriority
        binding?.callLogListCheck?.setOnCheckedChangeListener { _, _ ->
            Log.e("callLogTAG", "CallLogListFragment setOnCheckedChangeListener")
            searchDataList()
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callLogLiveData.safeSingleObserve(viewLifecycleOwner) { callLogList ->
                this@CallLogListFragment.callLogList = callLogList
                Log.e("callLogTAG",
                    "CallLogListFragment callLogLiveData callLogList.size ${callLogList.size}")
                if (!checkDataListEmptiness(callLogList)) {
                    getHashMapFromCallLogList(callLogList)
                }
            }
            callLogHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { callLogHashMap ->
                Log.e("callLogTAG",
                    "CallLogListFragment callLogHashMapLiveData callLogHashMap?.size ${callLogHashMap?.size}")
                binding?.root?.showMessage("callLogHashMapLiveData", false)
                callLogHashMap?.let { setDataList(it) }
                binding?.root?.showMessage("setDataList", false)
                Log.e("callLogTAG", "CallLogListFragment setDataList after")
            }
        }
    }

    override fun searchDataList() {
        Log.e("callLogTAG", "CallLogListFragment searchDataList")
        binding?.root?.showMessage("StartSearching", false)
        val filteredCallLogList = callLogList?.filter { callLog ->
            (callLog.name?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            ).isTrue() || callLog.phone?.lowercase(Locale.getDefault())?.contains(
                searchQuery?.lowercase(Locale.getDefault()).orEmpty()
            )
                .isTrue()) && if (binding?.callLogListCheck?.isChecked.isTrue()) callLog.type == BLOCKED_CALL else true
        } as? ArrayList<CallLog>
        Log.e("callLogTAG",
            "CallLogListFragment searchDataList filteredCallLogList?.size ${filteredCallLogList?.size}")
        filteredCallLogList?.apply {
            if (!checkDataListEmptiness(this)) {
                viewModel.getHashMapFromCallLogList(this)
            }
        }
        binding?.root?.showMessage("getHashMapFromCallLogList", false)
    }

    override fun getData() {
        Log.e("callLogTAG", "CallLogListFragment viewModel.getCallLogList()")
        viewModel.getCallLogList()
    }
}