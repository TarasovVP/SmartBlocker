package com.tarasovvp.blacklister.ui.main.call_list

import android.util.Log
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.constants.Constants
import com.tarasovvp.blacklister.databinding.FragmentCallListBinding
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.extensions.showPopUpWindow
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Info
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.base.BaseAdapter
import com.tarasovvp.blacklister.ui.base.BaseListFragment
import java.util.*

class CallListFragment :
    BaseListFragment<FragmentCallListBinding, CallListViewModel, Call>() {

    override var layoutId = R.layout.fragment_call_list
    override val viewModelClass = CallListViewModel::class.java

    private var callList: List<Call>? = null
    private var isDeleteMode = false

    override fun createAdapter(): BaseAdapter<Call>? {
        Log.e("adapterTAG", "CallListFragment createAdapter callList?.size ${callList?.size}")
        return context?.let {
            CallAdapter(object : CallClickListener {
                override fun onCallClick(number: String) {
                    findNavController().navigate(CallListFragmentDirections.startContactDetailFragment(
                        number = number))
                }

                override fun onCallLongClick() {
                    changeDeleteMode()
                }

                override fun onCallDeleteCheckChange(call: Call) {
                    callList?.find { it.time == call.time}?.isCheckedForDelete =
                        call.isCheckedForDelete
                    if (callList?.none { it.isCheckedForDelete }.isTrue() && isDeleteMode) {
                        changeDeleteMode()
                    }
                }

                override fun onCallDeleteInfoClick(view: View) {
                    view.showPopUpWindow(Info(title = getString(R.string.call_deleting),
                        description = getString(R.string.call_delete_info)))
                }
            })
        }
    }

    override fun initView() {
        binding?.apply {
            swipeRefresh = callListRefresh
            recyclerView = callListRecyclerView
            emptyStateContainer = callListEmpty
            setToolBarMenuClickListener()
            callListCheck.setOnCheckedChangeListener { _, checked ->
                getData()
                (activity as MainActivity).toolbar?.title =
                    getString(if (checked) R.string.log_list else R.string.blocked_call_log)
            }
            setFragmentResultListener(Constants.DELETE_FILTER) { _, _ ->
                viewModel.deleteCallList(callList?.filter { it.isCheckedForDelete }.orEmpty())
            }
        }
    }

    private fun setToolBarMenuClickListener() {
        Log.e("callTAG", "CallListFragment setToolBarMenuClickListener")
        (activity as MainActivity).toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_menu_item -> {
                    this@CallListFragment.findNavController()
                        .navigate(CallListFragmentDirections.startDeleteFilterDialog())
                    true
                }
                R.id.close_menu_item -> {
                    (adapter as CallAdapter).apply {
                        isDeleteMode = false
                        callList?.forEach {
                            it.isCheckedForDelete = false
                        }
                        notifyDataSetChanged()
                    }
                    true
                }
                else -> return@setOnMenuItemClickListener true
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
        (activity as MainActivity).toolbar?.apply {
            Log.e("callTAG", "CallListFragment menu $menu")
            menu?.clear()
            title = if (isDeleteMode) getString(R.string.delete_) else getString(if (binding?.callListCheck?.isChecked.isTrue()) R.string.log_list else R.string.blocked_call_log)
            inflateMenu(if (isDeleteMode) R.menu.toolbar_delete else R.menu.toolbar_search)
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callLiveData.safeSingleObserve(viewLifecycleOwner) { callListData ->
                callList = if (binding?.callListCheck?.isChecked.isTrue()) {
                    callList.orEmpty().plus(callListData).distinct()
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
        Log.e("callTAG", "CallListFragment searchDataList() start")
        val filteredCallList = callList?.filter { call ->
            (call.name?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue()
                    || call.number?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue())
        }.orEmpty()
        Log.e("callTAG",
            "CallListFragment searchDataList() filteredCallList size ${filteredCallList.size}")
        checkDataListEmptiness(filteredCallList)
        if (filteredCallList.isNotEmpty()) {
            viewModel.getHashMapFromCallList(filteredCallList)
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
