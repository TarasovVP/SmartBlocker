package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import com.tarasovvp.blacklister.utils.PermissionUtil.permissionsArray

abstract class BaseListFragment<VB : ViewBinding, T : BaseViewModel, D : BaseAdapter.MainData> :
    BaseFragment<VB, T>() {

    val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    var swipeRefresh: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null
    var searchableEditText: EditText? = null
    var emptyListText: TextView? = null

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun initView()
    abstract fun getDataList()
    abstract fun searchDataList()

    protected fun RecyclerView.initRecyclerView() {
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        this.adapter = this@BaseListFragment.adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        (activity as MainActivity).toolbar?.navigationIcon = null
        recyclerView?.initRecyclerView()
        if (context?.checkPermissions().isTrue()) {
            swipeRefresh?.isRefreshing = true
            getDataList()
        } else {
            permissionLauncher.launch(permissionsArray())
        }
        swipeRefresh?.setOnRefreshListener {
            getDataList()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).apply {
            toolbar?.setOnMenuItemClickListener {
                this@BaseListFragment.searchableEditText?.isVisible =
                    this@BaseListFragment.searchableEditText?.isVisible != true
                it.icon = ContextCompat.getDrawable(
                    this,
                    if (this@BaseListFragment.searchableEditText?.isVisible.isTrue()) R.drawable.ic_search_off else R.drawable.ic_search
                )
                if (this@BaseListFragment.searchableEditText?.isVisible != true) {
                    searchableEditText?.text?.clear()
                    searchDataList()
                }
                return@setOnMenuItemClickListener true
            }
        }
        searchableEditText?.doAfterTextChanged {
            searchDataList()
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false).isTrue()) {
                Toast.makeText(
                    context,
                    getString(R.string.give_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                getDataList()
            }
        }

    protected open fun checkDataListEmptiness(newData: List<D>): Boolean {
        emptyListText?.isVisible = newData.isNullOrEmpty()
        if (newData.isNullOrEmpty()) {
            adapter?.clearData()
            adapter?.notifyDataSetChanged()
        }
        swipeRefresh?.isRefreshing = false
        return newData.isEmpty()
    }

    protected open fun setDataList(dataListHashMap: HashMap<String, List<D>>) {
        adapter?.clearData()
        dataListHashMap.forEach { dataEntry ->
            adapter?.setHeaderAndData(
                dataEntry.value,
                HeaderDataItem(
                    headerType = HeaderDataItem.HEADER_TYPE,
                    header = dataEntry.key
                )
            )
            Log.e(
                "dataTAG",
                "BaseListFragment  dataListHashMap.forEach dataEntry.key ${dataEntry.key} dataEntry.value.size ${dataEntry.value.size}")
        }
        adapter?.notifyDataSetChanged()
        swipeRefresh?.isRefreshing = false
    }
}