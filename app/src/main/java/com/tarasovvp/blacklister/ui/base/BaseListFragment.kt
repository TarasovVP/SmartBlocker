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
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.tarasovvp.blacklister.R
import com.tarasovvp.blacklister.model.HeaderDataItem
import com.tarasovvp.blacklister.ui.MainActivity
import com.tarasovvp.blacklister.utils.PermissionUtil.checkPermissions
import com.tarasovvp.blacklister.utils.PermissionUtil.permissionsArray

abstract class BaseListFragment<VB : ViewBinding, T : ViewModel, D : BaseAdapter.MainData> :
    BaseFragment<VB, T>() {

    val adapter: BaseAdapter<D>? by lazy { createAdapter() }

    var swipeRefresh: SwipeRefreshLayout? = null
    var recyclerView: RecyclerView? = null
    var searchableEditText: EditText? = null
    var emptyListText: TextView? = null

    abstract fun createAdapter(): BaseAdapter<D>?
    abstract fun initView()
    abstract fun getDataList()
    abstract fun filterDataList()

    protected fun RecyclerView.initRecyclerView() {
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        this.adapter = this@BaseListFragment.adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkToolbarSearchVisibility()
        observeLiveData()
        initView()
        recyclerView?.initRecyclerView()
        if (context?.checkPermissions() == true) {
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
        searchableEditText?.doAfterTextChanged {
            filterDataList()
        }
    }

    private fun checkToolbarSearchVisibility() {
        (activity as MainActivity).apply {
            toolbar?.menu?.clear()
            if (navigationScreens.contains(findNavController().currentDestination?.id) && findNavController().currentDestination?.id != R.id.settingsFragment) {
                toolbar?.inflateMenu(R.menu.toolbar_search)
                toolbar?.setOnMenuItemClickListener {
                    this@BaseListFragment.searchableEditText?.isVisible =
                        this@BaseListFragment.searchableEditText?.isVisible != true
                    it.icon = ContextCompat.getDrawable(
                        this,
                        if (this@BaseListFragment.searchableEditText?.isVisible == true) R.drawable.ic_search_off else R.drawable.ic_search
                    )
                    if (this@BaseListFragment.searchableEditText?.isVisible != true) {
                        searchableEditText?.text?.clear()
                        filterDataList()
                    }
                    return@setOnMenuItemClickListener true
                }
            } else {
                toolbar?.menu?.clear()
            }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false) == true) {
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
        for (dataList in dataListHashMap) {
            adapter?.setHeaderAndData(
                dataList.value,
                HeaderDataItem(
                    headerType = HeaderDataItem.HEADER_TYPE,
                    header = dataList.key
                )
            )
        }
        Log.e(
            "dataTAG",
            "BaseListFragment setDataList  for (blackNumberEntry in blackNumberHashMap)"
        )
        adapter?.notifyDataSetChanged()
        swipeRefresh?.isRefreshing = false
    }
}