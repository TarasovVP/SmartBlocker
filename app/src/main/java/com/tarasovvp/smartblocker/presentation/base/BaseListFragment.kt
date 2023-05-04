package com.tarasovvp.smartblocker.presentation.base

import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.tarasovvp.smartblocker.R
import com.tarasovvp.smartblocker.domain.enums.EmptyState
import com.tarasovvp.smartblocker.domain.models.HeaderDataItem
import com.tarasovvp.smartblocker.domain.models.NumberData
import com.tarasovvp.smartblocker.presentation.main.MainActivity
import com.tarasovvp.smartblocker.presentation.main.number.list.list_call.ListCallFragment
import com.tarasovvp.smartblocker.presentation.main.number.list.list_contact.ListContactFragment
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListBlockerFragment
import com.tarasovvp.smartblocker.presentation.main.number.list.list_filter.ListPermissionFragment
import com.tarasovvp.smartblocker.utils.DebouncingQueryTextListener
import com.tarasovvp.smartblocker.utils.EmptyStateView
import com.tarasovvp.smartblocker.utils.extensions.*

abstract class BaseListFragment<B : ViewDataBinding, T : BaseViewModel, D : NumberData> :
    BaseNumberDataFragment<B, T>() {

    protected val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    protected var swipeRefresh: SwipeRefreshLayout? = null
    protected var recyclerView: RecyclerView? = null
    protected var filterCheck: CheckBox? = null
    protected var emptyStateContainer: EmptyStateView? = null
    protected var searchQuery: String? = String.EMPTY
    var filterIndexes: ArrayList<Int>? = null

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun searchDataList()
    abstract fun isFiltered(): Boolean

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRecyclerView()
        setSearchViewMenu()
        (activity as MainActivity).mainViewModel.successAllDataLiveData.safeSingleObserve(
            viewLifecycleOwner) {
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
            getData()
        }
    }

    protected fun setFilterCheck() {
        filterCheck?.apply {
            isSelected = true
            text = context?.numberDataFilteringText(filterIndexes ?: arrayListOf())
            isChecked = filterIndexes.isNullOrEmpty().not()
            isEnabled = adapter?.itemCount.orZero() > 0 || filterIndexes.isNullOrEmpty().not()
        }
    }

    protected fun setSearchViewMenu() {
        with(activity as MainActivity) {
            toolbar?.apply {
                inflateMenu(R.menu.toolbar_search)
                SearchView(this@with).apply {
                    if (this@BaseListFragment is ListBlockerFragment || this@BaseListFragment is ListPermissionFragment) inputType =
                        InputType.TYPE_CLASS_NUMBER
                    findViewById<AutoCompleteTextView>(androidx.appcompat.R.id.search_src_text)?.apply {
                        textSize = 16f
                    }
                    menu?.findItem(R.id.search_menu_item)?.let { menuItem ->
                        setQuery(searchQuery, false)
                        menuItem.actionView = this
                        menuItem.isVisible = adapter?.itemCount.orZero() > 0
                        isIconified = searchQuery.isNullOrEmpty()
                        maxWidth = Integer.MAX_VALUE
                    }
                    queryHint =
                        getString(if (this@BaseListFragment is ListBlockerFragment || this@BaseListFragment is ListPermissionFragment) R.string.list_filter_search_hint else R.string.list_number_search_hint)
                    setOnQueryTextListener(DebouncingQueryTextListener(lifecycle) {
                        searchQuery = it
                        searchDataList()
                    })
                    val contentInsetLeft = contentInsetLeft
                    setOnQueryTextFocusChangeListener { _, hasFocus ->
                        menu?.findItem(R.id.settings_menu_item)?.isVisible = hasFocus.not()
                        setContentInsetsAbsolute(if (hasFocus) 0 else contentInsetLeft,
                            contentInsetRight)
                        setPadding(if (hasFocus) dpToPx(8f).toInt() else 0,
                            0,
                            if (hasFocus) dpToPx(10f).toInt() else 0,
                            0)
                    }
                    clearFocus()
                }
                setOnMenuItemClickListener { menuItem ->
                    when (menuItem?.itemId) {
                        R.id.settings_menu_item -> findNavController().navigate(R.id.startSettingsListFragment)
                    }
                    return@setOnMenuItemClickListener true
                }
            }
        }
    }

    protected open fun checkDataListEmptiness(isEmpty: Boolean) {
        (activity as MainActivity).toolbar?.menu?.findItem(R.id.search_menu_item)?.isVisible =
            searchQuery.isNullOrEmpty().not() || isEmpty.not()
        emptyStateContainer?.isVisible = isEmpty
        recyclerView?.isVisible = isEmpty.not()
        emptyStateContainer?.setDescription(
            if (searchQuery.isNullOrEmpty() && isFiltered().not()) when (this) {
                is ListPermissionFragment -> EmptyState.EMPTY_STATE_PERMISSIONS.descriptionRes()
                is ListContactFragment -> EmptyState.EMPTY_STATE_CONTACTS.descriptionRes()
                is ListCallFragment -> EmptyState.EMPTY_STATE_CALLS.descriptionRes()
                else -> EmptyState.EMPTY_STATE_BLOCKERS.descriptionRes()
            } else EmptyState.EMPTY_STATE_QUERY.descriptionRes())
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