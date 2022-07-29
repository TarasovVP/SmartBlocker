package com.tarasovvp.blacklister.ui.main.call_list

import android.util.Log
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.databinding.FragmentCallListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.LogCall
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import java.util.*

class CallListFragment :
    BaseListFragment<FragmentCallListBinding, CallListViewModel, Call>() {

    override fun getViewBinding() = FragmentCallListBinding.inflate(layoutInflater)

    override val viewModelClass = CallListViewModel::class.java

    private var logCallList: List<Call>? = null

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
            Log.e("callLogTAG", "logCallListFragment setOnCheckedChangeListener")
            searchDataList()
            (activity as MainActivity).toolbar?.title =
                getString(if (checked) R.string.log_list else R.string.blocked_call_log)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            blockedCallLiveData.safeSingleObserve(viewLifecycleOwner) { blockedCallList ->
                this@CallListFragment.logCallList = blockedCallList
                Log.e("callLogTAG",
                    "logCallListFragment blockedCallLiveData blockedCallList.size ${blockedCallList.size}")
                if (checkDataListEmptiness(blockedCallList).not()) {
                    getHashMapFromCallList(blockedCallList)
                }
            }
            logCallLiveData.safeSingleObserve(viewLifecycleOwner) { logCallList ->
                this@CallListFragment.logCallList = logCallList
                Log.e("callLogTAG",
                    "logCallListFragment callLogLiveData logCallList.size ${logCallList.size}")
                if (checkDataListEmptiness(logCallList).not()) {
                    getHashMapFromCallList(logCallList)
                }
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
        val filteredLogCallList = logCallList?.filter { callLog ->
            (callLog.name?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue()
                    || callLog.phone?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue())
        } as? ArrayList<Call>
        Log.e("callLogTAG",
            "logCallListFragment searchDataList filteredlogCallList?.size ${filteredLogCallList?.size}")
        filteredLogCallList?.apply {
            if (checkDataListEmptiness(this).not()) {
                viewModel.getHashMapFromCallList(this)
            }
        }
    }

    override fun getData() {
        Log.e("callLogTAG", "logCallListFragment viewModel.getlogCallList()")
        viewModel.getBlockedCallList()
    }
}
