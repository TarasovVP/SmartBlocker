package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.IncludeEmptyStateBinding
import com.tarasovvp.blacklister.enums.EmptyState
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.hideKeyboard
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.number_data.NumberData
import com.tarasovvp.blacklister.ui.number_data.call_list.CallListFragment
import com.tarasovvp.blacklister.ui.number_data.contact_list.ContactListFragment
import com.tarasovvp.blacklister.ui.number_data.filter_list.WhiteFilterListFragment
import com.tarasovvp.blacklister.utils.DebouncingQueryTextListener

abstract class BaseListFragment<B : ViewDataBinding, T : BaseViewModel, D : NumberData> :
    BaseFragment<B, T>() {

    protected val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    protected var swipeRefresh: SwipeRefreshLayout? = null
    protected var recyclerView: RecyclerView? = null
    protected var emptyStateContainer: IncludeEmptyStateBinding? = null
    protected var searchQuery: String? = String.EMPTY

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun initView()
    abstract fun searchDataList()
    abstract fun getData()
    abstract fun isFiltered(): Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("adapterTAG",
            "BaseListFragment onViewCreated initView adapter $adapter itemCount ${adapter?.itemCount}")
        initView()
        setRecyclerView()
        setSearchViewMenu()
        getData()
        (activity as MainActivity).mainViewModel.successAllDataLiveData.safeSingleObserve(
            viewLifecycleOwner) {
            Log.e("callLogTAG", "BaseListFragment successAllDataLiveData getData()")
            this@BaseListFragment.getData()
        }
    }

    override fun onPause() {
        super.onPause()
        binding?.root?.hideKeyboard()
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
            toolbar?.inflateMenu(R.menu.toolbar_search)
            context?.let { SearchView(it) }?.apply {
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
        emptyStateContainer?.emptyState =
            if (searchQuery.isNullOrEmpty() && isFiltered().not()) when (this) {
                is WhiteFilterListFragment -> EmptyState.EMPTY_STATE_PERMISSIONS
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
                    headerType = HeaderDataItem.HEADER_TYPE,
                    header = dataEntry.key
                )
            )
        }
        adapter?.notifyDataSetChanged()
        swipeRefresh?.isRefreshing = false
    }
}