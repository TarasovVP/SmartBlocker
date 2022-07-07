package com.tarasovvp.blacklister.ui.base

import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.extensions.isTrue
import com.tarasovvp.blacklister.extensions.safeSingleObserve
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import com.tarasovvp.blacklister.utils.PermissionUtil.permissionsArray

abstract class BaseListFragment<VB : ViewBinding, T : BaseViewModel, D : BaseAdapter.MainData> :
    BaseFragment<VB, T>() {

    val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    var swipeRefresh: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null
    var emptyListText: TextView? = null
    protected var searchQuery: String? = ""

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun initView()
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
            getAllData()
        } else {
            permissionLauncher.launch(permissionsArray())
        }
        swipeRefresh?.setOnRefreshListener {
            getAllData()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).apply {
            val searchView =
                toolbar?.menu?.findItem(R.id.search_menu_item)?.actionView as? SearchView
            searchView?.queryHint = getString(R.string.enter_phone_number)
            searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    searchQuery = query
                    searchDataList()
                    return true
                }
            })
            toolbar?.menu?.findItem(R.id.settings_menu_item)?.setOnMenuItemClickListener {
                findNavController().navigate(R.id.startSettingsListFragment)
                return@setOnMenuItemClickListener true
            }
            toolbar?.menu?.findItem(R.id.notification_menu_item)?.setOnMenuItemClickListener {
                findNavController().navigate(R.id.startSettingsListFragment)
                return@setOnMenuItemClickListener true
            }
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
                getAllData()
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

    protected fun getAllData() {
        (activity as MainActivity).apply {
            getAllData()
        }
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
        }
        adapter?.notifyDataSetChanged()
        swipeRefresh?.isRefreshing = false
    }
}