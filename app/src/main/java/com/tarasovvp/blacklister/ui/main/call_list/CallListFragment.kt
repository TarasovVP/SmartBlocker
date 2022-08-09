package com.tarasovvp.blacklister.ui.main.call_list

import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentCallListBinding
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.local.SharedPreferencesUtil
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Info
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import com.tarasovvp.blacklister.utils.setSafeOnClickListener
import java.util.*

class CallListFragment :
    BaseListFragment<FragmentCallListBinding, CallListViewModel, Call>() {

    override fun getViewBinding() = FragmentCallListBinding.inflate(layoutInflater)

    override val viewModelClass = CallListViewModel::class.java

    private var callList: List<Call>? = null
    private var isDeleteMode = false

    override fun createAdapter(): BaseAdapter<Call>? {
        return context?.let {
            CallAdapter(object : CallClickListener {
                override fun onCallClick(phone: String) {
                    findNavController().navigate(CallListFragmentDirections.startNumberDetailFragment(
                        number = phone))
                }

                override fun onCallLongClick() {
                    changeDeleteMode()
                }

                override fun onCallDeleteCheckChange(call: Call) {
                    callList?.find { it.phone == call.phone }?.isCheckedForDelete = call.isCheckedForDelete
                    binding?.callListDeleteBtn?.isVisible = callList?.none { it.isCheckedForDelete }.isTrue().not()
                    binding?.callListDeleteAll?.isChecked = callList?.none { it.isCheckedForDelete.not() }.isTrue()
                }

                override fun onCallDeleteInfoClick(view: View) {
                    view.showPopUpWindow(Info(title = getString(R.string.call_deleting), description = getString(R.string.call_delete_info)))
                }
            })
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
        binding?.apply {
            swipeRefresh = callListRefresh
            recyclerView = callListRecyclerView
            emptyListText = callListEmpty
            priorityText = callListPriority
            callListCheck.setOnCheckedChangeListener { _, checked ->
                getData()
                (activity as MainActivity).toolbar?.title =
                    getString(if (checked) R.string.log_list else R.string.blocked_call_log)
            }
            callListDeleteAll.setOnCheckedChangeListener { _, checked ->
                callList?.forEach { it.isCheckedForDelete = checked }
                recyclerView?.post {
                    adapter?.notifyDataSetChanged()
                }
            }
            binding?.callListDeleteBtn?.setSafeOnClickListener {
                findNavController().navigate(CallListFragmentDirections.startDeleteNumberDialog())
            }
            setFragmentResultListener(Constants.DELETE_NUMBER) { _, _ ->
                viewModel.deleteCallList(callList?.filter { it.isCheckedForDelete }.orEmpty())
            }
        }
    }

    private fun changeDeleteMode() {
        isDeleteMode = isDeleteMode.not()
        (adapter as CallAdapter).apply {
            isDeleteMode = this@CallListFragment.isDeleteMode
            recyclerView?.post {
                adapter?.notifyDataSetChanged()
            }
        }
        binding?.apply {
            priorityText?.isVisible = isDeleteMode.not()
            callListCheck.isVisible = isDeleteMode.not()
            callListDeleteAll.isVisible = isDeleteMode
            callListDeleteBtn.isVisible =
                isDeleteMode && callList?.find { it.isCheckedForDelete }?.isNotNull().isTrue()
            if (isDeleteMode.not()) {
                callList?.forEach {
                    it.isCheckedForDelete = false
                }
                callListDeleteAll.isChecked = false
            }
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
                searchDataList()
            }
            callHashMapLiveData.safeSingleObserve(viewLifecycleOwner) { callHashMap ->
                callHashMap?.let { setDataList(it) }
            }
            successDeleteNumberLiveData.safeSingleObserve(viewLifecycleOwner) {
                (callList as ArrayList<Call>).removeAll { it.isCheckedForDelete }
                changeDeleteMode()
                searchDataList()
            }
        }
    }

    override fun searchDataList() {
        val filteredCallList = callList?.filter { call ->
            (call.name?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue()
                    || call.phone?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue())
        }
        filteredCallList?.apply {
            if (checkDataListEmptiness(this).not()) {
                viewModel.getHashMapFromCallList(this)
            }
        }
    }

    override fun getData() {
        if (binding?.callListCheck?.isChecked.isTrue()) {
            viewModel.getLogCallList()
        } else {
            viewModel.getBlockedCallList()
        }
    }
}
