package com.tarasovvp.blacklister.ui.main.call_list

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentCallListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import java.util.*

class CallListFragment :
    BaseListFragment<FragmentCallListBinding, CallListViewModel, Call>() {

    override fun getViewBinding() = FragmentCallListBinding.inflate(layoutInflater)

    override val viewModelClass = CallListViewModel::class.java

    private var callList: List<Call>? = null

    override fun createAdapter(): BaseAdapter<Call>? {
        return context?.let {
            CallAdapter { number ->
                findNavController().navigate(CallListFragmentDirections.startNumberDetailFragment(
                    number = number))
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as MainActivity).apply {
            if (SharedPreferencesUtil.blockTurnOff.not() && isBlockerLaunched().not()) startBlocker()
        }
    }

    override fun onResume() {
        super.onResume()
        binding?.callListCheck?.isChecked = false
    }

    override fun initView() {
        swipeRefresh = binding?.callListRefresh
        recyclerView = binding?.callListRecyclerView
        emptyListText = binding?.callListEmpty
        priorityText = binding?.callListPriority
        binding?.callListCheck?.setOnCheckedChangeListener { _, checked ->
            getData()
            (activity as MainActivity).toolbar?.title =
                getString(if (checked) R.string.log_list else R.string.blocked_call_log)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callLiveData.safeSingleObserve(viewLifecycleOwner) { callListData ->
                callList = if (binding?.callListCheck?.isChecked.isTrue()) {
                    callList.orEmpty().plus(callListData)
                } else {
                    callListData
                }

                Log.e("callLogTAG",
                    "logCallListFragment callLogLiveData callList.orEmpty() + logCallList.size ${callList?.size}")
                searchDataList()
            }
            callHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { callHashMap ->
                Log.e("callLogTAG",
                    "logCallListFragment callLogHashMapLiveData callLogHashMap?.size ${callHashMap?.size}")
                callHashMap?.let { setDataList(it) }
                Log.e("callLogTAG", "logCallListFragment setDataList after")
            }
        }
    }

    override fun searchDataList() {
        Log.e("callLogTAG", "logCallListFragment searchDataList")
        val filteredCallList = callList?.filter { call ->
            (call.name?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue()
                    || call.phone?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue())
        }
        Log.e("callLogTAG",
            "logCallListFragment searchDataList filteredlogCallList?.size ${filteredCallList?.size}")
        filteredCallList?.apply {
            if (checkDataListEmptiness(this).not()) {
                viewModel.getHashMapFromCallList(this)
            }
        }
    }

    override fun getData() {
        Log.e("callLogTAG", "logCallListFragment viewModel.getlogCallList()")
        if (binding?.callListCheck?.isChecked.isTrue()) {
            viewModel.getLogCallList()
        } else {
            viewModel.getBlockedCallList()
        }
    }
}
