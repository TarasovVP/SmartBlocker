package com.tarasovvp.blacklister.ui.number_data.call_list

import android.util.Log
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.FragmentCallListBinding
import com.tarasovvp.blacklister.enums.FilterAction
import com.tarasovvp.blacklister.extensions.*
import com.tarasovvp.blacklister.model.BlockedCall
import com.tarasovvp.blacklister.model.Call
import com.tarasovvp.blacklister.model.Contact
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
                override fun onCallClick(call: Call) {
                    findNavController().navigate(CallListFragmentDirections.startNumberDataDetailFragment(
                        Contact(name = call.name,
                            photoUrl = call.photoUrl,
                            number = call.number,
                            filterType = call.filterType)))
                }

                override fun onCallLongClick() {
                    changeDeleteMode()
                }

                override fun onCallDeleteCheckChange(call: Call) {
                    callList?.find { it.time == call.time }?.isCheckedForDelete =
                        call.isCheckedForDelete
                    if (callList?.any { it.isCheckedForDelete }.isTrue().not() && isDeleteMode) {
                        changeDeleteMode()
                    }
                }

                override fun onCallDeleteInfoClick() {
                    showMessage(getString(R.string.call_delete_info), true)
                }
            })
        }
    }

    override fun initView() {
        binding?.apply {
            swipeRefresh = callListRefresh
            recyclerView = callListRecyclerView
            emptyStateContainer = callListEmpty
            callListCheck.isEnabled = adapter?.itemCount.orZero() > 0 || callListCheck.isChecked
            callListRecyclerView.hideKeyboardWithLayoutTouch()
            callListCheck.setOnCheckedChangeListener { compoundButton, checked ->
                root.hideKeyboard()
                (activity as MainActivity).toolbar?.title =
                    getString(if (checked) R.string.blocked_call_log else R.string.log_list)
                if (compoundButton.isPressed) {
                    searchDataList()
                }
            }
            setFragmentResultListener(FilterAction.FILTER_ACTION_DELETE.name) { _, _ ->
                viewModel.deleteCallList(callList?.filter { it.isCheckedForDelete }.orEmpty())
            }
        }
    }

    private fun changeDeleteMode() {
        Log.e("destinationTAG", "CallListFragment changeDeleteMode isDeleteMode $isDeleteMode")
        isDeleteMode = isDeleteMode.not()
        (adapter as CallAdapter).apply {
            isDeleteMode = this@CallListFragment.isDeleteMode
            recyclerView?.post {
                adapter?.notifyDataSetChanged()
            }
        }
        (activity as MainActivity).toolbar?.apply {
            Log.e("callTAG", "CallListFragment menu $menu")
            title =
                if (isDeleteMode) getString(R.string.delete_) else getString(if (binding?.callListCheck?.isChecked.isTrue()) R.string.log_list else R.string.blocked_call_log)
            menu?.clear()
            if (isDeleteMode) {
                inflateMenu(R.menu.toolbar_delete)
                setDeleteMenuClickListener()
            } else {
                inflateMenu(R.menu.toolbar_search)
            }
        }
    }

    private fun setDeleteMenuClickListener() {
        Log.e("callTAG", "CallListFragment setToolBarMenuClickListener")
        (activity as MainActivity).toolbar?.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.delete_menu_item -> {
                    this@CallListFragment.findNavController()
                        .navigate(CallListFragmentDirections.startFilterActionDialog(filterAction = FilterAction.FILTER_ACTION_DELETE.name))
                    true
                }
                R.id.close_menu_item -> {
                    (adapter as CallAdapter).apply {
                        isDeleteMode = false
                        callList?.forEach {
                            it.isCheckedForDelete = false
                        }
                        changeDeleteMode()
                        notifyDataSetChanged()
                    }
                    true
                }
                else -> return@setOnMenuItemClickListener true
            }
        }
    }

    override fun observeLiveData() {
        with(viewModel) {
            callListLiveData.safeSingleObserve(viewLifecycleOwner) { callListData ->
                if (callListData == callList) {
                    checkDataListEmptiness(callListData.isEmpty())
                    return@safeSingleObserve
                }
                callList = callListData
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

    override fun isFiltered(): Boolean {
        return binding?.callListCheck?.isChecked.isTrue()
    }

    override fun searchDataList() {
        Log.e("callTAG", "CallListFragment searchDataList() start")
        (adapter as? CallAdapter)?.searchQuery = searchQuery.orEmpty()
        val filteredCallList = callList?.filter { call ->
            (call.name?.lowercase(Locale.getDefault())
                ?.contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue()
                    || call.number.lowercase(Locale.getDefault())
                .contains(searchQuery?.lowercase(Locale.getDefault()).orEmpty()).isTrue())
                    && if (binding?.callListCheck?.isChecked.isTrue()) call is BlockedCall else true
        }.orEmpty()
        Log.e("callTAG",
            "CallListFragment searchDataList() filteredCallList size ${filteredCallList.size}")
        binding?.callListCheck?.isEnabled = filteredCallList.isNotEmpty() || binding?.callListCheck?.isChecked.isTrue()
        checkDataListEmptiness(filteredCallList.isEmpty())
        if (filteredCallList.isNotEmpty()) {
            viewModel.getHashMapFromCallList(filteredCallList)
        }
    }

    override fun getData() {
        viewModel.getCallList()
    }
}
