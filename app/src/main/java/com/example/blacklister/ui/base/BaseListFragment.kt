package com.example.blacklister.ui.base

import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewbinding.ViewBinding
import com.example.blacklister.R
import com.example.blacklister.utils.PermissionUtil.checkPermissions
import com.example.blacklister.utils.PermissionUtil.permissionsArray

abstract class BaseListFragment<VB : ViewBinding, T : ViewModel, M : Any> : BaseFragment<VB, T>() {

    val adapter: BaseAdapter<M, *>? by lazy { createAdapter() }

    var swipeRefresh: SwipeRefreshLayout? = null

    abstract fun createAdapter(): BaseAdapter<M, *>?
    abstract fun getDataList()

    protected fun RecyclerView.initRecyclerView() {
        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        this.adapter = this@BaseListFragment.adapter
    }

    override fun onResume() {
        super.onResume()
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

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, @JvmSuppressWildcards Boolean>? ->
            if (isGranted?.values?.contains(false) == true) {
                Toast.makeText(
                    context,
                    getString(R.string.glve_all_permissions),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                getDataList()
            }
        }

    protected open fun onInitialDataLoaded(newData: List<M>) {
        swipeRefresh?.isRefreshing = false
        adapter?.apply {
            clear()
            addAll(newData)
            notifyDataSetChanged()
        }
    }

    protected open fun updateListItems(newData: List<M>) {
        adapter?.updateListItems(newData, null)
    }
}