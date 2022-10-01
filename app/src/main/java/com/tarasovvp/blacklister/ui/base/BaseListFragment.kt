package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.databinding.IncludeEmptyStateBinding
import com.tarasovvp.blacklister.extensions.EMPTY
import com.tarasovvp.blacklister.extensions.hideKeyboard
import com.tarasovvp.blacklister.extensions.orZero
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.ui.main.call_list.CallListFragment
import com.tarasovvp.blacklister.ui.main.contact_list.ContactListFragment
import com.tarasovvp.blacklister.ui.main.filter_list.BlackFilterListFragment
import com.tarasovvp.blacklister.ui.main.filter_list.WhiteFilterListFragment
import com.tarasovvp.blacklister.utils.DebouncingQueryTextListener

abstract class BaseListFragment<B : ViewDataBinding, T : BaseViewModel, D : BaseAdapter.MainData> :
    BaseFragment<B, T>() {

    val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    var swipeRefresh: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null
    var emptyStateContainer: IncludeEmptyStateBinding? = null
    protected var searchQuery: String? = String.EMPTY

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun initView()
    abstract fun searchDataList()
    abstract fun getData()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("adapterTAG", "BaseListFragment onViewCreated initView adapter $adapter itemCount ${adapter?.itemCount}")
        setSearchViewMenu()
        initView()
        setRecyclerView()
        (activity as MainActivity).mainViewModel.successAllDataLiveData.safeSingleObserve(viewLifecycleOwner) {
            Log.e("callLogTAG", "BaseListFragment successAllDataLiveData getData()")
            this@BaseListFragment.getData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (adapter?.itemCount.orZero() == 0) {
            getData()
        } else {
            searchDataList()
        }
    }

    private fun setRecyclerView() {
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = this@BaseListFragment.adapter
        }
        swipeRefresh?.setOnRefreshListener {
            getData()
        }
    }

    private fun setSearchViewMenu() {
        context?.let { SearchView(it) }?.apply {
            (activity as MainActivity).toolbar?.menu?.findItem(R.id.search_menu_item)?.let {
                it.actionView = this
                it.isVisible = false
            }
            queryHint = getString(R.string.enter_phone_number)
            setOnQueryTextListener(DebouncingQueryTextListener(lifecycle) {
                searchQuery = it
                searchDataList()
                context?.hideKeyboard(this)
            })
        }

    }

    protected open fun checkDataListEmptiness(newData: List<D>, isFiltered: Boolean) {
        (activity as MainActivity).toolbar?.menu?.findItem(R.id.search_menu_item)?.isVisible = searchQuery.isNullOrEmpty().not() || newData.isNotEmpty()
        emptyStateContainer?.root?.isVisible = newData.isEmpty()
        emptyStateContainer?.emptyStateTitle?.text = if (searchQuery.isNullOrEmpty() && isFiltered.not()) when (this) {
            is BlackFilterListFragment -> getString(R.string.black_list_empty_state)
            is WhiteFilterListFragment -> getString(R.string.white_list_empty_state)
            is ContactListFragment -> getString(R.string.contact_list_empty_state)
            is CallListFragment -> getString(R.string.call_list_empty_state)
            else -> String.EMPTY
        } else getString(R.string.no_ruslt_with_list_query)
        emptyStateContainer?.emptyStateIcon?.setImageResource(R.drawable.ic_empty_state)
        if (newData.isEmpty()) {
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