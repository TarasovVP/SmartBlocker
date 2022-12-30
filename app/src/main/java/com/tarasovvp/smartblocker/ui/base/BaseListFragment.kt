package com.tarasovvp.smartblocker.ui.base

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.databinding.IncludeEmptyStateBinding
import com.tarasovvp.smartblocker.enums.EmptyState
import com.tarasovvp.smartblocker.extensions.EMPTY
import com.tarasovvp.smartblocker.extensions.orZero
import com.tarasovvp.smartblocker.extensions.safeSingleObserve
import com.tarasovvp.smartblocker.models.HeaderDataItem
import com.tarasovvp.smartblocker.models.NumberData
import com.tarasovvp.smartblocker.ui.MainActivity
import com.tarasovvp.smartblocker.ui.number_data.list.call_list.CallListFragment
import com.tarasovvp.smartblocker.ui.number_data.list.contact_list.ContactListFragment
import com.tarasovvp.smartblocker.ui.number_data.list.filter_list.BlockerListFragment
import com.tarasovvp.smartblocker.ui.number_data.list.filter_list.PermissionListFragment
import com.tarasovvp.smartblocker.utils.DebouncingQueryTextListener

abstract class BaseListFragment<B : ViewDataBinding, T : BaseViewModel, D : NumberData> :
    BaseNumberDataFragment<B, T>() {

    protected val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    protected var swipeRefresh: SwipeRefreshLayout? = null
    protected var recyclerView: RecyclerView? = null
    protected var emptyStateContainer: IncludeEmptyStateBinding? = null
    protected var searchQuery: String? = String.EMPTY

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun searchDataList()
    abstract fun isFiltered(): Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("adapterTAG",
            "BaseListFragment onViewCreated initView adapter $adapter itemCount ${adapter?.itemCount}")
        setRecyclerView()
        setSearchViewMenu()
        (activity as MainActivity).mainViewModel.successAllDataLiveData.safeSingleObserve(
            viewLifecycleOwner) {
            Log.e("callLogTAG", "BaseListFragment successAllDataLiveData getData()")
            this@BaseListFragment.getData()
        }
    }


    private fun setRecyclerView() {
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            this.adapter = this@BaseListFragment.adapter
        }
        swipeRefresh?.setOnRefreshListener {
            Log.e("adapterTAG",
                "BaseListFragment setOnRefreshListener this $this itemCount ${adapter?.itemCount}")
            getData()
        }
    }

    private fun setSearchViewMenu() {
        (activity as MainActivity).apply {
            Log.e("searchTAG", "BaseListFragment setSearchViewMenu searchQuery $searchQuery")
            toolbar?.inflateMenu(R.menu.toolbar_search)
            context?.let { SearchView(it) }?.apply {
                if (this@BaseListFragment is BlockerListFragment || this@BaseListFragment is PermissionListFragment) inputType =
                    InputType.TYPE_CLASS_NUMBER
                toolbar?.menu?.findItem(R.id.search_menu_item)?.let { menuItem ->
                    setQuery(searchQuery, false)
                    menuItem.actionView = this
                    menuItem.isVisible = adapter?.itemCount.orZero() > 0
                    isIconified = searchQuery.isNullOrEmpty()
                }
                queryHint = getString(R.string.enter_phone_number)
                setOnQueryTextListener(DebouncingQueryTextListener(lifecycle) {
                    searchQuery = it
                    searchDataList()
                })
                clearFocus()
            }
            toolbar?.setOnMenuItemClickListener { menuItem ->
                when (menuItem?.itemId) {
                    R.id.settings_menu_item -> findNavController().navigate(R.id.startSettingsListFragment)
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    protected open fun checkDataListEmptiness(isEmpty: Boolean) {
        (activity as MainActivity).toolbar?.menu?.findItem(R.id.search_menu_item)?.isVisible =
            searchQuery.isNullOrEmpty().not() || isEmpty.not()
        emptyStateContainer?.root?.isVisible = isEmpty
        swipeRefresh?.isVisible = isEmpty.not()
        emptyStateContainer?.emptyState =
            if (searchQuery.isNullOrEmpty() && isFiltered().not()) when (this) {
                is PermissionListFragment -> EmptyState.EMPTY_STATE_PERMISSIONS
                is ContactListFragment -> EmptyState.EMPTY_STATE_CONTACTS
                is CallListFragment -> EmptyState.EMPTY_STATE_CALLS
                else -> EmptyState.EMPTY_STATE_BLOCKERS
            } else EmptyState.EMPTY_STATE_QUERY
        if (isEmpty) {
            adapter?.clearData()
            adapter?.notifyDataSetChanged()
        }
        swipeRefresh?.isRefreshing = false
    }

    protected open fun setDataList(dataListHashMap: Map<String, List<D>>) {
        adapter?.clearData()
        dataListHashMap.forEach { dataEntry ->
            adapter?.setHeaderAndData(
                dataEntry.value,
                HeaderDataItem(
                    header = dataEntry.key
                )
            )
        }
        adapter?.notifyDataSetChanged()
        swipeRefresh?.isRefreshing = false
    }
}