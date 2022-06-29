package com.tarasovvp.blacklister.ui.main.callloglist

import android.content.IntentFilter
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants.BLACK_NUMBER
import com.tarasovvp.blacklister.constants.Constants.BLOCKED_CALL
import com.tarasovvp.blacklister.constants.Constants.CALL_RECEIVE
import com.tarasovvp.blacklister.databinding.FragmentCallLogListBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.BlackNumber
import com.tarasovvp.blacklister.model.CallLog
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.ui.main.contactlist.ContactListFragmentDirections
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
            CallLogAdapter { callLog, view ->
                val listener = PopupMenu.OnMenuItemClickListener { item ->
                    when (item?.itemId) {
                        R.id.change -> {
                            if (callLog.isBlackList) {
                                findNavController().navigate(CallLogListFragmentDirections.startInfoDialog(
                                    blackNumber = BlackNumber(blackNumber = callLog.phone.toString())))
                            } else {
                                findNavController().navigate(CallLogListFragmentDirections.startNumberAddFragment(
                                    BlackNumber(blackNumber = callLog.phone.toString())))
                            }
                        }
                        R.id.details -> {
                            findNavController().navigate(ContactListFragmentDirections.startNumberDetailFragment(
                                number = callLog.phone))
                        }
                    }
                    true
                }
                it.showPopUpMenu(if (callLog.isBlackList) R.menu.number_delete_menu else R.menu.number_add_menu,
                    view,
                    listener)
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
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<BlackNumber>(
            BLACK_NUMBER)?.safeSingleObserve(viewLifecycleOwner) { blackNumber ->
            viewModel.deleteBlackNumber(blackNumber)
        }
        binding?.callLogListCheck?.setOnCheckedChangeListener { _, _ ->
            searchDataList()
        }
    }

    private fun getAllData() {
        (activity as MainActivity).apply {
            getAllData()
            mainViewModel.successAllDataLiveData.safeSingleObserve(this, { success ->
                viewModel.getCallLogList()
            })
        }
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
            deleteSuccessLiveData.safeSingleObserve(viewLifecycleOwner, {
                getAllData()
            })
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